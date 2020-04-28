package nl.lijstr.repositories.movies;

import nl.lijstr.domain.movies.MovieCollection;
import nl.lijstr.repositories.abs.BasicRepository;

import java.util.List;

/**
 * Repository for fetching movie collections.
 */
public interface MovieCollectionRepository extends BasicRepository<MovieCollection> {

    /**
     * Find a collection by searching the title and keywords
     * @param title
     * @param keyword
     * @return the found collections
     */
    List<MovieCollection> findByTitleContainingOrKeywordsContaining(String title, String keyword);

}
