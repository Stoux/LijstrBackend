package nl.lijstr.api.movies;

import java.util.List;
import nl.lijstr.api.abs.base.TargetRatingEndpoint;
import nl.lijstr.api.abs.base.models.ExtendedRating;
import nl.lijstr.api.abs.base.models.post.RatingRequest;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieRating;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.movies.MovieRatingRepository;
import nl.lijstr.repositories.movies.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint for adding/editing ratings on a {@link Movie}.
 */
@Secured(Permission.MOVIE_USER)
@RestController
@RequestMapping(value = "/movies/{id:\\d+}/ratings", produces = "application/json")
public class MovieRatingEndpoint extends TargetRatingEndpoint<Movie, MovieRating, ExtendedRating, RatingRequest> {

    @Autowired
    public MovieRatingEndpoint(MovieRepository targetRepository, MovieRatingRepository ratingRepository) {
        super(targetRepository, ratingRepository, "Show");
    }

    @Override
    protected ExtendedRating convertRating(MovieRating rating) {
        return new ExtendedRating(rating);
    }

    @Override
    protected MovieRating createNewRating(Movie movie, User user, RatingRequest request) {
        return new MovieRating(movie, user, request.getSeen(), request.getRating(), request.getComment());
    }

    @Override
    protected List<MovieRating> getLatestRatings(Movie target) {
        return target.getLatestMovieRatings();
    }

}
