package nl.lijstr.repositories.movies;

import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieUserMeta;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.abs.BasicMovieRepository;

/**
 * Repository for fetching user meta data.
 */
public interface MovieUserMetaRepository extends BasicMovieRepository<MovieUserMeta> {

    /**
     * Find a user's meta data on a movie.
     *
     * @param movie The movie
     * @param user  The user
     *
     * @return the user meta or null
     */
    MovieUserMeta findByMovieAndUser(Movie movie, User user);

}
