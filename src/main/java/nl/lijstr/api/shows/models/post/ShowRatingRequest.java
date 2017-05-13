package nl.lijstr.api.shows.models.post;

import java.math.BigDecimal;
import lombok.*;
import nl.lijstr.api.abs.base.models.post.RatingRequest;
import nl.lijstr.domain.base.RatingModel;

/**
 * Created by Stoux on 13/05/2017.
 */
@Getter
@NoArgsConstructor
public class ShowRatingRequest extends RatingRequest {

    private boolean ignoreRating;

    /**
     * Create a rating request.
     *
     * @param seen    Seen status
     * @param rating  A rating
     * @param comment An optional comment
     */
    public ShowRatingRequest(RatingModel.Seen seen, BigDecimal rating, String comment) {
        super(seen, rating, comment);
        this.ignoreRating = false;
    }

    /**
     * Create a rating request that ignores the rating.
     *
     * @param comment An optional comment
     */
    public ShowRatingRequest(RatingModel.Seen seen, String comment) {
        super(seen, null, comment);
        this.ignoreRating = true;
    }
}
