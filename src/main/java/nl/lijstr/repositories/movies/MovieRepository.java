package nl.lijstr.repositories.movies;

import nl.lijstr.domain.movies.Movie;
import nl.lijstr.repositories.abs.BasicRepository;
import org.springframework.stereotype.Repository;

/**
 * The basic Movie repository.
 */
@Repository
public interface MovieRepository extends BasicRepository<Movie> {

}
