package nl.lijstr.api.abs.base.models.post;

import java.math.BigDecimal;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import lombok.*;
import nl.lijstr.domain.base.RatingModel;
import nl.lijstr.domain.movies.MovieRating;

/**
 * A request model to add a rating.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequest {

    private RatingModel.Seen seen;

    @Getter(AccessLevel.NONE)
    @DecimalMin(value = "1.0", message = "A rating can't be less than 1")
    @DecimalMax(value = "10.0", message = "A rating can't be more than 10")
    private BigDecimal rating;

    private String comment;

    public BigDecimal getRating() {
        if (seen == MovieRating.Seen.YES) {
            return rating;
        } else {
            return null;
        }
    }
}
