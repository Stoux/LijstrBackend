package nl.lijstr.domain.shows;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.*;
import nl.lijstr.domain.base.RatingModel;
import nl.lijstr.domain.users.User;

/**
 * A rating a user can give for a show.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class ShowRating extends RatingModel<Show> {

    @Column(nullable = false)
    private Boolean ignoreRating;

    @Column(precision = 3, scale = 1)
    private BigDecimal generatedRating;

    /**
     * Create a new rating for a show.
     *
     * @param user    The user
     * @param show    The show
     * @param seen    Has seen the target
     * @param rating  The actual rating (can be null for ?)
     * @param comment An optional comment
     */
    public ShowRating(User user, Show show, Seen seen, BigDecimal rating, String comment) {
        super(user, show, seen, rating, comment);
        this.ignoreRating = true;
    }

    /**
     * Create a new rating for a show that uses the average of the season ratings.
     *
     * @param user    The user
     * @param show    The show
     * @param seen    Has seen the target
     * @param comment An optional comment
     */
    public ShowRating(User user, Show show, Seen seen, String comment) {
        super(user, show, seen, null, comment);
        this.ignoreRating = false;
    }

    @Override
    public BigDecimal getRating() {
        return ignoreRating ? generatedRating : super.getRating();
    }
}
