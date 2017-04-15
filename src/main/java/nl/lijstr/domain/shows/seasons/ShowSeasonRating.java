package nl.lijstr.domain.shows.seasons;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.*;
import nl.lijstr.domain.base.RatingModel;
import nl.lijstr.domain.users.User;

/**
 * A rating a user can give for a show's season.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class ShowSeasonRating extends RatingModel<ShowSeason> {

    @Column(nullable = false)
    private Boolean ignoreRating;

    /**
     * Create a new rating for a show's season.
     *
     * @param user    The user
     * @param season  The show's season
     * @param seen    Has seen the target
     * @param rating  The actual rating (can be null for ?)
     * @param comment An optional comment
     */
    public ShowSeasonRating(User user, ShowSeason season, Seen seen, BigDecimal rating, String comment) {
        super(user, season, seen, rating, comment);
        this.ignoreRating = true;
    }

    /**
     * Create a new rating for a show that uses the average of the episode ratings.
     *
     * @param user    The user
     * @param season  The show's season
     * @param seen    Has seen the target
     * @param comment An optional comment
     */
    public ShowSeasonRating(User user, ShowSeason season, Seen seen, String comment) {
        super(user, season, seen, null, comment);
        this.ignoreRating = false;
    }

}
