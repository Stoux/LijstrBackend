package nl.lijstr.repositories.movies;

import java.time.LocalDate;
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
     * Find the movie with the oldest lastUpdated value.
     *
     * @return the movie
     */
    Movie findFirstByOrderByLastUpdatedAsc();

    /**
     * Find the movie with the oldest lastUpdated value that has a release date after the given value.
     *
     * @param released The release date
     *
     * @return the movie
     */
    Movie findFirstByReleasedAfterOrderByLastUpdatedAsc(LocalDate released);

    /**
     * Find a movie by it's IMDB ID.
     *
     * @param imdbId The IMDB ID
     *
     * @return the movie or null
     */
    Movie findByImdbId(String imdbId);

}
