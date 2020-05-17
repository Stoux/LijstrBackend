package nl.lijstr.cron;

import java.time.LocalDate;
import javax.transaction.Transactional;

import io.sentry.Sentry;
import nl.lijstr.common.Utils;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.exceptions.LijstrException;
import nl.lijstr.processors.annotations.InjectLogger;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.services.maf.MafApiService;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Stoux on 29/03/2017.
 */
@Component
public class MovieUpdateTask {

    @InjectLogger
    private Logger logger;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MafApiService mafApiService;

    @Value("${maf.schedule-updates}")
    private boolean runUpdates;

    /**
     * Update the movie with the oldest lastUpdated value.
     */
    //Runs every 0th, 10th, 20th and 30th minute past every hour, every day
    @Transactional
    @Scheduled(cron = "0 0,10,20,30 * * * *")
    public void updateOldestByCron() {
        if (!runUpdates) {
            return;
        }

        Utils.withSentry(() -> {
            Movie movie = movieRepository.findFirstByOrderByLastUpdatedAsc();
            update(movie);
        });
    }

    /**
     * Update the movie with the oldest lastUpdated value that has been recently released (or has yet to be released).
     */
    //Runs every 40th and 50th minute past every hour, every day
    @Transactional
    @Scheduled(cron = "0 40,50 * * * *")
    public void updateOldestFromRecentMovies() {
        if (!runUpdates) {
            return;
        }

        Utils.withSentry(() -> {
            LocalDate recentDate = LocalDate.now().minusYears(1);
            Movie movie = movieRepository.findFirstByReleasedAfterOrderByLastUpdatedAsc(recentDate);
            update(movie);
        });
    }

    private void update(Movie movie) {
        if (movie == null) {
            logger.warn("[CRON] No movie to update found.");
        } else {
            logger.info("[CRON] Updating movie: {} ({})", movie.getTitle(), movie.getImdbId());
            mafApiService.updateMovie(movie);
            logger.info("[CRON] Finished updating movie");
        }
    }

}
