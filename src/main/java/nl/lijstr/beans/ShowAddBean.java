package nl.lijstr.beans;

import java.util.Optional;
import nl.lijstr.domain.shows.Show;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.BadRequestException;
import nl.lijstr.processors.annotations.InjectLogger;
import nl.lijstr.repositories.shows.ShowRepository;
import nl.lijstr.services.maf.MafApiService;
import nl.lijstr.services.omdb.OmdbApiService;
import nl.lijstr.services.omdb.models.OmdbObject;
import nl.lijstr.services.tvmaze.TvMazeService;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Bean for utility methods regarding the ability to add shows.
 */
@Component
public class ShowAddBean {

    @InjectLogger
    private Logger logger;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private OmdbApiService omdbApiService;

    @Autowired
    private MafApiService mafApiService;

    @Autowired
    private TvMazeService tvMazeService;

    /**
     * Add a new show.
     *
     * @param addedBy   Added by this user
     * @param imdbId    The IMDB ID of the show
     * @param youtubeId An optional youtube ID
     *
     * @return the new show
     */
    public Show addShow(User addedBy, String imdbId, String youtubeId) {
        ///Check if already added
        if (showRepository.findByImdbId(imdbId) != null) {
            throw new BadRequestException("Show already added");
        }


        //Add the show
        OmdbObject omdbObject = omdbApiService.getShow(imdbId);
        logger.info("Creating new Show: {} ({})", omdbObject.getTitle(), imdbId);
        Show newShow = new Show(imdbId, omdbObject.getTitle(), youtubeId, addedBy);
        showRepository.save(newShow);

        //Update using MAF & TvMaze
        Show updatedShow = mafApiService.updateShow(newShow);
        return addTvMazeData(updatedShow);
        //TODO: Update episodes ratings
    }

    private Show addTvMazeData(Show show) {
        Optional<Long> tvMazeId = tvMazeService.getTvMazeId(show.getImdbId());
        if (!tvMazeId.isPresent()) {
            logger.info("No TvMaze ID found for show: {} ({})", show.getTitle(), show.getImdbId());
            return show;
        }

        show.setTvMazeId(tvMazeId.get());
        return tvMazeService.updateShow(show);
    }

}
