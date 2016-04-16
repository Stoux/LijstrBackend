package nl.lijstr.repositories.imdb;

import nl.lijstr.domain.imdb.Person;
import nl.lijstr.repositories.abs.BasicRepository;

/**
 * A basic IMDB Actor/Person repository.
 */
public interface PersonRepository extends BasicRepository<Person> {

    /**
     * Get a Person by their IMDB ID.
     *
     * @param imdbId The ID
     *
     * @return the person or null
     */
    Person getByImdbId(String imdbId);

}
