package nl.lijstr.api.movies;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.validation.Valid;
import nl.lijstr.api.abs.AbsMovieService;
import nl.lijstr.api.movies.models.MovieExtendedRating;
import nl.lijstr.api.movies.models.post.MovieRatingRequest;
import nl.lijstr.common.DataContainer;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieRating;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.db.ConflictException;
import nl.lijstr.repositories.movies.MovieRatingRepository;
import nl.lijstr.security.model.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Stoux on 17/04/2016.
 */
@RestController
@RequestMapping(value = "/movies/{movieId:\\d+}/ratings", produces = "application/json")
public class MovieRatingEndpoint extends AbsMovieService {

    /**
     * The number of minutes that is considered recent.
     * Used to force user to edit and/or add.
     */
    public static final long RECENT_MINUTES = 30L;

    @Autowired
    private MovieRatingRepository ratingRepository;

    /**
     * Get the latest rating a user has given on a movie.
     *
     * @param movieId The movie ID
     *
     * @return a container containing the rating or null (if the user hasn't given a rating)
     */
    @RequestMapping(path = "/latest/")
    public DataContainer<MovieExtendedRating> getLatestRatingForUser(@PathVariable Long movieId) {
        Movie movie = findMovie(movieId);
        JwtUser user = getUser();
        MovieRating rating = ratingRepository.findByMovieAndUserAndLatest(movie, new User(user.getId()), true);
        return new DataContainer<>(rating == null ? null : new MovieExtendedRating(rating));
    }

    /**
     * Add a new rating.
     *
     * @param movieId   The movie's ID
     * @param newRating The new Rating
     *
     * @return a short version of the rating
     */
    @RequestMapping(method = RequestMethod.POST)
    public MovieExtendedRating add(@PathVariable Long movieId, @Valid @RequestBody MovieRatingRequest newRating) {
        JwtUser user = getUser();
        Movie movie = findMovie(movieId);
        MovieRating addedRating = addRating(user, movie, newRating);
        return new MovieExtendedRating(addedRating);
    }

    @Transactional
    private MovieRating addRating(JwtUser user, Movie movie, MovieRatingRequest newRating) {
        //Check if the user already has an existing rating
        MovieRating existingRating = findRatingByUser(movie.getLatestMovieRatings(), user.getId());
        if (existingRating != null) {
            if (isRecent(existingRating)) {
                throw new ConflictException("Modify the old rating (ID: " + existingRating.getId() + ")");
            }

            existingRating.setLatest(false);
            ratingRepository.save(existingRating);
        }

        //Add the new rating
        MovieRating.Seen seen = newRating.getSeen();
        MovieRating rating = new MovieRating(
                movie, new User(user.getId()), seen, newRating.getRating(), newRating.getComment()
        );
        return ratingRepository.saveAndFlush(rating);
    }

    /**
     * Update an existing rating.
     *
     * @param movieId   The movie's ID
     * @param ratingId  The rating's ID
     * @param newRating The new rating
     *
     * @return the updated rating
     */
    @RequestMapping(value = "/{ratingId:\\d+}", method = RequestMethod.PUT)
    public MovieExtendedRating edit(@PathVariable Long movieId, @PathVariable Long ratingId,
                                 @Valid @RequestBody MovieRatingRequest newRating) {
        JwtUser user = getUser();
        Movie movie = findMovie(movieId);

        //Find the rating
        MovieRating latestRating = findRatingByUser(movie.getLatestMovieRatings(), user.getId());
        if (latestRating == null || !latestRating.getId().equals(ratingId) || !isRecent(latestRating)) {
            throw new ConflictException("Unable to find rating (either non existent, old or not yours)");
        }

        //Update the rating
        latestRating.setSeen(newRating.getSeen());
        latestRating.setRating(newRating.getRating());
        latestRating.setComment(newRating.getComment());
        MovieRating updatedRating = ratingRepository.saveAndFlush(latestRating);

        return new MovieExtendedRating(updatedRating);
    }

    private MovieRating findRatingByUser(List<MovieRating> ratings, Long userId) {
        for (MovieRating rating : ratings) {
            if (rating.getUser().getId().equals(userId)) {
                return rating;
            }
        }
        return null;
    }

    private boolean isRecent(MovieRating rating) {
        long minutesUntilNow = rating.getCreated().until(LocalDateTime.now(), ChronoUnit.MINUTES);
        return minutesUntilNow < RECENT_MINUTES;
    }

}
