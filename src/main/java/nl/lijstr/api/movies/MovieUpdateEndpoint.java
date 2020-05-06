package nl.lijstr.api.movies;

import java.util.Collections;
import nl.lijstr.api.abs.AbsService;
import nl.lijstr.api.movies.models.MovieSummary;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.processors.annotations.InjectLogger;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.services.maf.MafApiService;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * A RestController that has endpoints for updating Movies.
 */
@Secured(Permission.ADMIN)
@RestController
@RequestMapping(value = "/movies/update", produces = "application/json")
public class MovieUpdateEndpoint extends AbsService {

    @InjectLogger
    private Logger logger;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MafApiService mafApiService;

    /**
     * Update the movie with the oldest 'lastUpdated' value.
     *
     * @return a summary view of the updated movie
     */
    @RequestMapping(value = "/oldest", method = RequestMethod.PUT)
    public MovieSummary updateOldest() {
        logger.debug("Request to update the oldest movie");
        Movie movie = movieRepository.findFirstByOrderByLastUpdatedAsc();
        checkIfFound(movie);
        logger.info("Updating movie: {} ({})", movie.getTitle(), movie.getImdbId());
        mafApiService.updateMovie(movie);
        return MovieSummary.convert(
            movie, false, false, false, false, false, false, Collections.emptySet()
        );
    }

    /**
     * Update a movie by it's IMDB ID.
     *
     * @param imdbId The ID of the movie
     */
    @RequestMapping(value = "/{imdbId:tt\\d{7,8}}", method = RequestMethod.PUT)
    public void updateMovie(@PathVariable("imdbId") String imdbId) {
        logger.debug("Request to update movie: {}", imdbId);
        Movie movie = movieRepository.findByImdbId(imdbId);
        checkIfFound(movie, "Movie", imdbId);
        logger.info("Updating movie: {} ({})", movie.getTitle(), movie.getImdbId());
        mafApiService.updateMovie(movie);
    }

}
