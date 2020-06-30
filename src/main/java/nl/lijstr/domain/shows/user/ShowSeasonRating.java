package nl.lijstr.domain.shows.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.lijstr.domain.base.IdCmUserModel;
import nl.lijstr.domain.shows.ShowSeason;
import org.thymeleaf.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * Model of a rating for a {@link ShowSeason}.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ShowSeasonRating extends IdCmUserModel {

    @JsonBackReference
    @ManyToOne
    private ShowSeason season;

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
