package nl.lijstr.api.movies;

import java.util.List;
import java.util.stream.Collectors;
import nl.lijstr.api.abs.AbsRestService;
import nl.lijstr.api.movies.models.MovieSummary;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.repositories.abs.BasicRepository;
import nl.lijstr.repositories.movies.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Movies Endpoint.
 */
@RestController
@RequestMapping(value = "/movies", produces = "application/json")
public class MovieEndpoint extends AbsRestService<Movie> {

    @Autowired
    private MovieRepository movieRepository;

    /**
     * Create a new MovieEndpoint.
     */
    public MovieEndpoint() {
        super("Movie");
    }

    @Override
    protected BasicRepository<Movie> getRestRepository() {
        return movieRepository;
    }

    /**
     * Get a list of short versions of all movies.
     *
     * @param includeGenres    Should include the movie's genres
     * @param includeLanguages Should include the movie's languages
     *
     * @return the list
     */
    @RequestMapping("/summaries")
    public List<MovieSummary> summaries(
            @RequestParam(required = false, defaultValue = "false") final boolean includeGenres,
            @RequestParam(required = false, defaultValue = "false") final boolean includeLanguages) {
        return movieRepository.findAll()
                .stream()
                .map(m -> MovieSummary.convert(m, includeGenres, includeLanguages))
                .collect(Collectors.toList());
    }

}
