package nl.lijstr.api.shows;

import java.util.List;
import nl.lijstr.api.abs.base.TargetRatingEndpoint;
import nl.lijstr.api.abs.base.models.ExtendedRating;
import nl.lijstr.api.shows.models.post.ShowRatingRequest;
import nl.lijstr.domain.shows.Show;
import nl.lijstr.domain.shows.ShowRating;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.shows.ShowRatingRepository;
import nl.lijstr.repositories.shows.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint for adding/editing ratings on a {@link Show}.
 */
@Secured(Permission.SHOW_USER)
@RestController
@RequestMapping(value = "/shows/{id:\\d+}/ratings", produces = "application/json")
public class ShowRatingEndpoint extends TargetRatingEndpoint<Show, ShowRating, ExtendedRating, ShowRatingRequest> {

    @Autowired
    public ShowRatingEndpoint(ShowRepository targetRepository, ShowRatingRepository ratingRepository) {
        super(targetRepository, ratingRepository, "Show");
    }

    @Override
    protected ExtendedRating convertRating(ShowRating rating) {
        return new ExtendedRating(rating);
    }

    @Override
    protected ShowRating createNewRating(Show show, User user, ShowRatingRequest request) {
        if (request.isIgnoreRating()) {
            return new ShowRating(user, show, request.getSeen(), request.getComment());
        } else {
            return new ShowRating(user, show, request.getSeen(), request.getRating(), request.getComment());
        }
    }

    @Override
    protected List<ShowRating> getLatestRatings(Show show) {
        return show.getLatestShowRatings();
    }
}
