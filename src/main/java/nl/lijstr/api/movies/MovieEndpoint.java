package nl.lijstr.api.movies;

import nl.lijstr.api.abs.AbsRestService;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.repositories.abs.BasicRepository;
import nl.lijstr.repositories.movies.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
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

}
