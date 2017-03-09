package nl.lijstr.api.movies;

import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import nl.lijstr.api.abs.AbsService;
import nl.lijstr.api.movies.models.MovieExtendedRating;
import nl.lijstr.api.movies.models.MovieShortDetail;
import nl.lijstr.api.movies.models.MovieStats;
import nl.lijstr.api.movies.models.wrappers.MovieRecentChange;
import nl.lijstr.common.PageResult;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieComment;
import nl.lijstr.domain.movies.MovieRating;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.abs.BasicRepository;
import nl.lijstr.repositories.movies.MovieCommentRepository;
import nl.lijstr.repositories.movies.MovieRatingRepository;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.repositories.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint for fetching general stats (about movies & user ratings).
 */
@RestController
@RequestMapping(value = "/movies/stats", produces = "application/json")
public class MovieStatsEndpoint extends AbsService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRatingRepository ratingRepository;

    @Autowired
    private MovieCommentRepository commentRepository;

    /**
     * Get the movie stats.
     *
     * @return the stats
     */
    @RequestMapping(method = RequestMethod.GET)
    public MovieStats getStats() {
        List<Movie> all = movieRepository.findAll();
        List<User> users = userRepository.findByGrantedPermissionsPermissionName(Permission.MOVIE_USER);

        MovieStats stats = new MovieStats(users);
        stats.setNumberOfMovies(all.size());
        stats.setAverageImdb(avg(all, Movie::getImdbRating));
        stats.setAverageMetacritic(avg(all, Movie::getMetacriticScore));

        for (Movie movie : all) {
            stats.incrementYear(movie.getYear());

            User addedBy = movie.getAddedBy();
            if (addedBy != null) {
                MovieStats.UserStats addedByStats = stats.getUser(addedBy.getId());
                if (addedByStats != null) {
                    addedByStats.added++;
                }
            }

            List<MovieRating> ratings = movie.getLatestMovieRatings();
            for (MovieRating rating : ratings) {
                MovieStats.UserStats userStats = stats.getUser(rating.getUser().getId());
                userStats.filledIn++;

                switch (rating.getSeen()) {
                    case YES:
                        userStats.seen++;
                        if (rating.getRating() == null) {
                            userStats.unknownRating++;
                        } else {
                            userStats.addRating(rating.getRating().doubleValue());
                        }
                        break;
                    case NO:
                        userStats.notSeen++;
                        break;
                    case UNKNOWN:
                        userStats.noIdea++;
                        break;
                }

                if (rating.getComment() != null) {
                    userStats.withComment++;
                }
            }
        }

        return stats;
    }

    private <X> double avg(List<X> items, Function<X, Number> getNumberFunction) {
        OptionalDouble average = items.stream()
            .map(getNumberFunction)
            .filter(Objects::nonNull)
            .flatMapToDouble(x -> DoubleStream.of(x.doubleValue()))
            .average();

        if (average.isPresent()) {
            return average.getAsDouble();
        } else {
            return 0;
        }
    }

    /**
     * Get recently added movies.
     *
     * @param page  The requested page (one-based)
     * @param limit Number of entries to return
     *
     * @return the movies
     */
    @RequestMapping(value = "/added", method = RequestMethod.GET)
    public PageResult<MovieShortDetail> recentlyAddedMovies(
        @RequestParam(required = false, defaultValue = "1") @Min(1) int page,
        @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(100) int limit) {
        return getPaged(movieRepository, page, limit, MovieShortDetail::fromMovie);
    }

    /**
     * Get new ratings.
     *
     * @param page  The requested page (one-based)
     * @param limit Number of entries to return
     *
     * @return the ratings
     */
    @RequestMapping(value = "/newRatings")
    public PageResult<MovieRecentChange> newRatings(
        @RequestParam(required = false, defaultValue = "1") @Min(1) int page,
        @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(100) int limit) {
        return getPaged(ratingRepository, page, limit, r -> wrapRating(r.getMovie(), new MovieExtendedRating(r)));
    }

    /**
     * Get new comments.
     *
     * @param page  The requested page (one-based)
     * @param limit Number of entries to return
     *
     * @return the comments
     */
    @RequestMapping(value = "/newComments", method = RequestMethod.GET)
    public PageResult<MovieRecentChange<MovieComment>> newComments(
        @RequestParam(required = false, defaultValue = "1") @Min(1) int page,
        @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(100) int limit) {
        return getPaged(commentRepository, page, limit, c -> wrapRating(c.getMovie(), c));
    }

    private <X, Y> PageResult<Y> getPaged(BasicRepository<X> repository, int page, int limit, Function<X, Y> mapper) {
        Pageable p = new PageRequest(page - 1, limit, Sort.Direction.DESC, "created");
        Page<X> pagedResult = repository.findAll(p);

        List<Y> content = pagedResult.getContent().stream()
            .map(mapper)
            .collect(Collectors.toList());
        return new PageResult<>(page, limit, pagedResult.getTotalElements(), pagedResult.getTotalPages(), content);
    }

    private <X> MovieRecentChange<X> wrapRating(Movie movie, X change) {
        return new MovieRecentChange<>(
            MovieShortDetail.fromMovie(movie),
            change
        );
    }


}
