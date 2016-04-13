package nl.lijstr.repositories.imdb;

import nl.lijstr.domain.imdb.SpokenLanguage;
import nl.lijstr.repositories.abs.BasicRepository;

/**
 * A basic IMDB Language repository.
 */
public interface SpokenLanguageRepository extends BasicRepository<SpokenLanguage> {

    /**
     * Get a Language object by it's language.
     *
     * @param language The language
     *
     * @return the language object or null
     */
    SpokenLanguage getByLanguage(String language);

}
