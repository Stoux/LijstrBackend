package nl.lijstr.repositories.movies;

import java.time.LocalDate;
import java.util.List;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.repositories.abs.BasicRepository;
import nl.lijstr.repositories.abs.BasicTargetRepository;

/**
 * The basic Movie repository.
 */
public interface MovieRepository extends BasicTargetRepository<Movie> {

    /**
     * Find the movie with the oldest lastUpdated value that has a release date after the given value.
     *
     * @param released The release date
     *
     * @return the movie
     */
    Movie findFirstByReleasedAfterOrderByLastUpdatedAsc(LocalDate released);

}
