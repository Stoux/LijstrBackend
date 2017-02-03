package nl.lijstr.repositories.movies;

import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieRating;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.abs.BasicMovieRepository;

/**
 * A repository for Movie comments.
 */
public interface MovieRatingRepository extends BasicMovieRepository<MovieRating> {

    /**
     * Find a rating by move, user and latest values.
     * Recommended to use latest = true (as that should always return one value).
     *
     * @param movie  The movie
     * @param user   The user
     * @param latest Latest movie
     *
     * @return the rating
     */
    MovieRating findByMovieAndUserAndLatest(Movie movie, User user, Boolean latest);

}
