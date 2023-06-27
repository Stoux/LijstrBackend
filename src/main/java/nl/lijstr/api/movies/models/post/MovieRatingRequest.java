package nl.lijstr.api.movies.models.post;

import lombok.*;
import nl.lijstr.domain.movies.MovieRating;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

/**
 * Created by Stoux on 20/04/2016.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieRatingRequest {

    private MovieRating.Seen seen;

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
