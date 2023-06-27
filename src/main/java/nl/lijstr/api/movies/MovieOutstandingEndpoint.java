package nl.lijstr.api.movies;

import nl.lijstr.api.abs.AbsService;
import nl.lijstr.api.movies.models.MovieDetail;
import nl.lijstr.api.movies.models.wrappers.MovieOutstandingCount;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieRating;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.security.model.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Endpoint for getting outstanding movies (regarding a user).
 */
@Secured(Permission.MOVIE_USER)
@RestController
@RequestMapping(value = "/movies/outstanding", produces = "application/json")
public class MovieOutstandingEndpoint extends AbsService {

    @Autowired
    private MovieRepository movieRepository;

    /**
     * Get the total count of movies that aren't filled in for the given user.
     *
     * @return the total count
     */
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public MovieOutstandingCount countWithoutRating() {
        JwtUser user = getUser();
        long total = filteredStream(user.getId()).count();
        return new MovieOutstandingCount(total);
    }


    /**
     * Get all the movies without a rating.
     *
     * @return the list of movies
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<MovieDetail> getWithoutRating() {
        JwtUser jwtUser = getUser();
        return filteredStream(jwtUser.getId())
                .map(MovieDetail::fromMovie)
                .collect(Collectors.toList());
    }

    private boolean hasRating(long userId, Movie movie) {
        Optional<MovieRating> any = movie.getLatestMovieRatings().stream()
                .filter(movieRating -> movieRating.getUser().getId().equals(userId))
                .findAny();
        return any.isPresent();
    }

    private Stream<Movie> filteredStream(long userId) {
        return movieRepository.findAllByOrderByTitleAsc().stream()
                .filter(m -> !hasRating(userId, m));
    }

}
