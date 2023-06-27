package nl.lijstr.api.other;

import nl.lijstr.common.Utils;
import nl.lijstr.domain.imdb.Genre;
import nl.lijstr.domain.imdb.SpokenLanguage;
import nl.lijstr.repositories.imdb.GenreRepository;
import nl.lijstr.repositories.imdb.SpokenLanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by Stoux on 22-10-2016.
 */
@RestController
@RequestMapping(produces = "application/json")
public class ImdbEndpoint {

    private final GenreRepository genreRepository;
    private final SpokenLanguageRepository languageRepository;

    @Autowired
    public ImdbEndpoint(GenreRepository genreRepository, SpokenLanguageRepository languageRepository) {
        this.genreRepository = genreRepository;
        this.languageRepository = languageRepository;
    }

    /**
     * Get all genres as a map.
     *
     * @return the genres
     */
    @RequestMapping("/genres")
    public Map<Long, String> getGenres() {
        return Utils.toMap(genreRepository.findAll(), Genre::getId, Genre::getGenre);
    }

    /**
     * Get all languages as a map.
     *
     * @return the languages
     */
    @RequestMapping("/languages")
    public Map<Long, String> getLanguages() {
        return Utils.toMap(languageRepository.findAll(), SpokenLanguage::getId, SpokenLanguage::getLanguage);
    }

}
