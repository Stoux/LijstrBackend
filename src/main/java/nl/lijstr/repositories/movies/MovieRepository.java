package nl.lijstr.repositories.movies;

import java.util.List;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.repositories.abs.BasicRepository;

/**
 * The basic Movie repository.
 */
public interface MovieRepository extends BasicRepository<Movie> {

    /**
     * Get all movies ordered by title.
     *
     * @return list of movies
     */
    List<Movie> findAllByOrderByTitleAsc();

    /**
     * Find the last movie that has been updated.
     *
     * @return the movie
     */
    Movie findFirstByOrderByLastUpdatedAsc();

    /**
     * Find a movie by it's IMDB ID.
     *
     * @param imdbId The IMDB ID
     *
     * @return the movie or null
     */
    Movie findByImdbId(String imdbId);

}
