package nl.lijstr.api.shows;

import java.util.List;
import javax.validation.Valid;
import nl.lijstr.api.abs.base.TargetEndpoint;
import nl.lijstr.api.abs.base.models.post.PostedRequest;
import nl.lijstr.api.shows.models.ShowDetail;
import nl.lijstr.api.shows.models.ShowSummary;
import nl.lijstr.beans.ShowAddBean;
import nl.lijstr.domain.shows.Show;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.shows.ShowRepository;
import nl.lijstr.security.model.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * The Shows Endpoint.
 */
@RestController
@RequestMapping(value = "/shows", produces = "application/json")
public class ShowEndpoint extends TargetEndpoint<Show, ShowRepository> {

    private final ShowAddBean addBean;

    @Autowired
    public ShowEndpoint(ShowRepository targetRepository, ShowAddBean addBean) {
        super(targetRepository, "show");
        this.addBean = addBean;
    }

    /**
     * Get a {@link Show} as detail view.
     *
     * @param id The ID of the show
     *
     * @return the show detail
     */
    @RequestMapping(DETAIL_PATH)
    public ShowDetail getById(@PathVariable("id") final long id) {
        Show show = findOne(targetRepository, id, "movie");
        return ShowDetail.fromShow(show);
    }

    /**
     * Get the original {@link Show}.
     *
     * @param id The ID of the show
     *
     * @return the show
     */
    @Secured(Permission.SHOW_MOD)
    @RequestMapping(ORIGINAL_PATH)
    public Show getOriginalById(@PathVariable("id") final long id) {
        return findOne(targetRepository, id, "show");
    }

    /**
     * Get a list of summaries of all shows.
     *
     * @param requestedUsers A comma separated list of all the requested users which will only return their ratings
     *
     * @return the list
     */
    @RequestMapping
    public List<ShowSummary> summaries(@RequestParam(required = false, name = "users") final String requestedUsers) {
        return summaryList(requestedUsers, ShowSummary::convert);
    }

    /**
     * Add a new Movie to the DB.
     *
     * @param postedRequest The data
     *
     * @return detail representation of the show
     */
    @Secured(Permission.SHOW_MOD)
    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    public ShowDetail addShow(@Valid @RequestBody PostedRequest postedRequest) {
        //Validate
        JwtUser user = getUser();
        Show show = addBean.addShow(new User(user.getId()), postedRequest.getImdbId(), postedRequest.getYoutubeId());
        return ShowDetail.fromShow(show);
    }

}
