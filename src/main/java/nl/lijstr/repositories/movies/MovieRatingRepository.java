package nl.lijstr.repositories.movies;

import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieRating;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.abs.BasicMovieRepository;
import nl.lijstr.repositories.abs.BasicRatingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * A repository for {@link MovieRating}s.
 */
public interface MovieRatingRepository extends BasicRatingRepository<Movie, MovieRating> {

}
