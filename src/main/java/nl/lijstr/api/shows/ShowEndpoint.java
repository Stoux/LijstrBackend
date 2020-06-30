package nl.lijstr.api.shows;

import nl.lijstr.api.abs.AbsShowService;
import nl.lijstr.api.shows.models.ShowSummary;
import nl.lijstr.api.shows.models.post.PostedShowRequest;
import nl.lijstr.beans.ShowBean;
import nl.lijstr.beans.shows.ShowSummaryBean;
import nl.lijstr.domain.shows.Show;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.domain.users.User;
import nl.lijstr.security.model.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Endpoint for TV Shows.
 */
@RestController
@RequestMapping(value = "/shows", produces = MediaType.APPLICATION_JSON_VALUE)
public class ShowEndpoint extends AbsShowService {

    @Autowired
    private ShowBean showBean;

    @Autowired
    private ShowSummaryBean showSummaryBean;


    /**
     * Get a {@link Show} as detail view.
     *
     * @param id The ID of the show
     * @return the show detail
     */
    @RequestMapping("/{id}")
    public Show getById(@PathVariable("id") final long id) {
        // TODO: Implement trimmed down detail view
        return findShow(id);
    }

    /**
     * Get the original {@link Show}.
     *
     * @param id The ID of the show
     * @return the show
     */
    @Secured({Permission.SHOW_MOD, Permission.ADMIN})
    @RequestMapping("/{id}/original")
    public Show getOriginalById(@PathVariable("id") final long id) {
        return findShow(id);
    }

    /**
     * Get a list of summaries of all shows.
     *
     * @return list of show summaries
     */
    @RequestMapping
    public List<ShowSummary> summaries() {
        return showSummaryBean.buildSummaries();
    }

    /**
     * Add a new {@link Show}.
     *
     * @param postedRequest The request with the ID
     * @return the new show
     */
    @Secured(Permission.SHOW_MOD)
    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    public Show addShow(@Valid @RequestBody PostedShowRequest postedRequest) {
        final JwtUser jwtUser = getUser();
        final User user = new User(jwtUser.getId());

        // Resolve to tmdbId;
        final Show newShow;
        if (postedRequest.getTmdbId() != null) {
            // TMDB ID is in the request
            newShow = showBean.addShowByTmdbId(postedRequest.getTmdbId(), user);
        } else {
            // Resolve from IMDB ID
            newShow = showBean.addShowByImdbId(postedRequest.getImdbId(), user);
        }

        // TODO: Return detail view.
        return newShow;
    }


}
