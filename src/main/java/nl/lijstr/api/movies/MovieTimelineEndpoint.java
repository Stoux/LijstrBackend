package nl.lijstr.api.movies;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nl.lijstr.api.abs.AbsMovieService;
import nl.lijstr.api.movies.models.MovieExtendedRating;
import nl.lijstr.api.movies.models.MovieShortComment;
import nl.lijstr.api.movies.models.TimeBased;
import nl.lijstr.domain.movies.Movie;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint for returning a timeline of events regarding a movie.
 */
@RestController
@RequestMapping(value = "/movies/{movieId:\\d+}/timeline", produces = "application/json")
public class MovieTimelineEndpoint extends AbsMovieService {

    /**
     * Get an ordered list of all comments and ratings that belong to the specified movie.
     *
     * @param movieId The movie's ID
     *
     * @return a list of comments and ratings.
     */
    @RequestMapping()
    public List<TimeBased> list(@PathVariable() Long movieId) {
        Movie movie = findMovie(movieId);

        //TODO: Only fetch a part of the timeline instead of the whole thing in 1 go.
        //Possibly ways to do this are using a UNION SQL call:
        //  => SELECT fields, 1 FROM rating UNION SELECT fields, 2 FROM comment ORDER BY last_modified DESC
        //  => Fetch the returned IDs from their respective tables

        //Build the list of entities
        Stream<TimeBased> commentStram = movie.getMovieComments().stream().map(MovieShortComment::new);
        Stream<TimeBased> ratingSteam = movie.getMovieRatings().stream().map(MovieExtendedRating::new);
        return Stream.concat(commentStram, ratingSteam)
                .sorted()
                .collect(Collectors.toList());
    }

}
