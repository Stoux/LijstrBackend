package nl.lijstr.api.movies;

import javax.transaction.Transactional;
import nl.lijstr.api.abs.AbsService;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.processors.annotations.InjectLogger;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.services.maf.MafApiService;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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

    private boolean shouldRun = false;

    /**
     * Update the movie that was last updated.
     */
    @RequestMapping(value = "/oldest", method = RequestMethod.PUT)
    public void updateOldest() {
        logger.debug("Request to update the oldest movie");
        Movie movie = movieRepository.findFirstByOrderByLastUpdatedAsc();
        checkIfFound(movie);
        logger.info("Updating movie: {} ({})", movie.getTitle(), movie.getImdbId());
        mafApiService.updateMovie(movie);
    }

    /**
     * Update a movie by it's IMDB ID.
     *
     * @param imdbId The ID of the movie
     */
    @RequestMapping(value = "/{imdbId:tt\\d{7}}", method = RequestMethod.PUT)
    public void updateMovie(@PathVariable("imdbId") String imdbId) {
        logger.debug("Request to update movie: {}", imdbId);
        Movie movie = movieRepository.findByImdbId(imdbId);
        checkIfFound(movie, "Movie", imdbId);
        logger.info("Updating movie: {} ({})", movie.getTitle(), movie.getImdbId());
        mafApiService.updateMovie(movie);
    }

    /**
     * Update the oldest movie.
     */
    //@Scheduled(cron = "0 0 */6 * * *")
    @Scheduled(cron = "0 * * * * *")
    public void updateOldestByCron() {
        if (!shouldRun) {
            return;
        }

        this.shouldRun = false;
        this.updateOldestCron();
    }

    @Transactional
    private void updateOldestCron() {
        logger.debug("[CRON] Updating oldest movie");
        Movie movie = movieRepository.findFirstByOrderByLastUpdatedAsc();
        if (movie == null) {
            logger.debug("[CRON] No movie to update found.");
        } else {
            logger.info("[CRON] Updating movie: {} ({})", movie.getTitle(), movie.getImdbId());
            mafApiService.updateMovie(movie);
            logger.info("[CRON] Finished updating movie");
        }
    }

    @RequestMapping(value = "/activate", method = RequestMethod.POST)
    public void activate() {
        this.shouldRun = true;
    }


}
