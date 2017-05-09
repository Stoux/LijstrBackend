package nl.lijstr.beans;

import nl.lijstr.domain.shows.Show;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.BadRequestException;
import nl.lijstr.repositories.shows.ShowRepository;
import nl.lijstr.services.maf.MafApiService;
import nl.lijstr.services.omdb.OmdbApiService;
import nl.lijstr.services.omdb.models.OmdbObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Bean for utility methods regarding the ability to add shows.
 */
@Component
public class ShowAddBean {

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private OmdbApiService omdbApiService;

    @Autowired
    private MafApiService mafApiService;

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


        OmdbObject omdbObject = omdbApiService.getShow(imdbId);
        Show newShow = new Show(imdbId, omdbObject.getTitle(), youtubeId, addedBy);
        showRepository.save(newShow);
        return mafApiService.updateShow(newShow);
        //TODO: Update show using TV Maze data
        //TODO: Update episodes ratings
    }

}
