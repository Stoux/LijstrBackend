package nl.lijstr.api.movies;

import java.util.List;
import java.util.stream.Collectors;
import nl.lijstr.api.abs.AbsRestService;
import nl.lijstr.api.movies.models.MovieSummary;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.repositories.abs.BasicRepository;
import nl.lijstr.repositories.movies.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
     * @param includeGenres    Should include genres
     * @param includeLanguages Should include languages
     * @param includeAgeRating Should include age rating
     *
     * @return the list
     */
    @RequestMapping("/summaries")
    public List<MovieSummary> summaries(
            @RequestParam(required = false, defaultValue = "false") final boolean includeGenres,
            @RequestParam(required = false, defaultValue = "false") final boolean includeLanguages,
            @RequestParam(required = false, defaultValue = "false") final boolean includeAgeRating) {
        return movieRepository.findAll()
                .stream()
                .map(m -> MovieSummary.convert(m, includeGenres, includeLanguages, includeAgeRating))
                .collect(Collectors.toList());
    }

    /**
     * Request a Movie.
     *
     * @param imdbId The IMDB ID
     */
    @Transactional
    @RequestMapping(value = "/request", method = RequestMethod.POST)
    public void requestMovie(@RequestParam() final String imdbId) {
        //TODO: Get user
        //TODO: Check if right to request a movie
        //TODO: If not; Forbidden
    }

    /**
     * Add a new Movie to the DB.
     *
     * @param imdbId The IMDB ID
     */
    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    public void addMovie(@RequestParam() final String imdbId) {
        //TODO: Get user
        //TODO: Check if right to add a movie
        //TODO: If not; Forbidden
    }



}
