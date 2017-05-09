package nl.lijstr.domain.shows.episodes;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.*;
import nl.lijstr.domain.base.RatingModel;
import nl.lijstr.domain.users.User;

/**
 * A rating a user can give for a show's episode.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class ShowEpisodeRating extends RatingModel<ShowEpisode> {

    @Column(nullable = false)
    private Boolean ignoreRating;

    /**
     * Create a new rating for a show's episode.
     *
     * @param user    The user
     * @param episode The show's episode
     * @param rating  The actual rating (can be null for ?)
     * @param comment An optional comment
     */
    public ShowEpisodeRating(User user, ShowEpisode episode, BigDecimal rating, String comment) {
        super(user, episode, Seen.YES, rating, comment);
        this.ignoreRating = false;
    }

    /**
     * Create a new rating for a show's episode for keeping track of which episodes the user has seen.
     *
     * @param user    The user
     * @param episode The show's episode
     * @param comment An optional comment
     */
    public ShowEpisodeRating(User user, ShowEpisode episode, String comment) {
        super(user, episode, Seen.YES, null, comment);
        this.ignoreRating = true;
    }

}
