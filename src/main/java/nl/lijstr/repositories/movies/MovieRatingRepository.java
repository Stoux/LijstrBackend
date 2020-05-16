package nl.lijstr.repositories.movies;

import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieRating;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.abs.BasicMovieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

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


    /**
     * Find all (paged) ratings with a given Seen value.
     *
     * @param pageable The requested page
     * @param seen     The requested seen value
     *
     * @return a paged result
     */
    Page<MovieRating> findAllBySeenEquals(Pageable pageable, MovieRating.Seen seen);

    /**
     * Find all ratings not by a given user and after a given time.
     *
     * @param user The user
     * @param lastModified The date time
     *
     * @return list of ratings
     */
    List<MovieRating> findAllByUserNotAndLastModifiedAfterAndLatestIsTrue(User user, LocalDateTime lastModified);

}
