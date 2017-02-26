package nl.lijstr.api.movies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import nl.lijstr.api.abs.AbsService;
import nl.lijstr.api.movies.models.MovieStats;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieRating;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.repositories.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    /**
     * Get the movie stats.
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

                switch(rating.getSeen()) {
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


}
