package nl.lijstr.beans;

import java.util.function.Function;
import nl.lijstr.domain.imdb.Genre;
import nl.lijstr.domain.imdb.Person;
import nl.lijstr.domain.imdb.SpokenLanguage;
import nl.lijstr.repositories.imdb.GenreRepository;
import nl.lijstr.repositories.imdb.PersonRepository;
import nl.lijstr.repositories.imdb.SpokenLanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Stoux on 01/03/2016.
 */
@Component
public class ImdbBean {

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private SpokenLanguageRepository languageRepository;

    @Autowired
    private PersonRepository personRepository;

    /**
     * Try to find a Genre object, otherwise create it.
     *
     * @param genre The genre
     *
     * @return the object
     */
    public Genre getOrCreateGenre(String genre) {
        return getOrCreate(genre, genreRepository::getByGenre, Genre::new, genreRepository::saveAndFlush);
    }

    /**
     * Try to find a Language object, otherwise create it.
     *
     * @param language The language
     *
     * @return the object
     */
    public SpokenLanguage getOrCreateLanguage(String language) {
        return getOrCreate(language, languageRepository::getByLanguage, SpokenLanguage::new, languageRepository::saveAndFlush);
    }

    /**
     * Find a person/actor by their IMDB ID.
     *
     * @param imdbId The ID
     *
     * @return the person or null
     */
    public Person getPerson(String imdbId) {
        return personRepository.getByImdbId(imdbId);
    }

    /**
     * Add a person to the DB.
     *
     * @param person The person
     *
     * @return the person
     */
    public Person addPerson(Person person) {
        return personRepository.save(person);
    }

    private static <X> X getOrCreate(String findValue,
                                     Function<String, X> findMethod,
                                     Function<String, X> createMethod,
                                     Function<X, X> saveMethod) {
        X x = findMethod.apply(findValue);
        if (x == null) {
            x = createMethod.apply(findValue);
            return saveMethod.apply(x);
        } else {
            return x;
        }
    }


}
