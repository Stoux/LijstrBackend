package nl.lijstr.domain.shows.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.lijstr.domain.base.IdCmUserModel;
import nl.lijstr.domain.shows.Show;
import nl.lijstr.domain.shows.meta.RatingType;
import nl.lijstr.domain.shows.meta.ShowSeen;
import org.thymeleaf.util.StringUtils;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Model for a rating of a {@link Show}.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ShowRating extends IdCmUserModel {

    @JsonBackReference
    @ManyToOne
    private Show show;

    /**
     * Seen status of this show. Notice that this can be changed without creating a new
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShowSeen seen;

    /**
     * Describes the type of {@link ShowRating#rating} that has been left (if any).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RatingType ratingType;

    /**
     * See {@link ShowRating#ratingType}
     */
    @Column(precision = 3, scale = 1)
    private BigDecimal rating;

    @Column(length = 5000)
    private String comment;

    @Column(nullable = false)
    private Boolean latest;

    /**
     * Check if this rating has a comment.
     *
     * @return has comment
     */
    public boolean hasComment() {
        return !StringUtils.isEmpty(this.comment);
    }

}
