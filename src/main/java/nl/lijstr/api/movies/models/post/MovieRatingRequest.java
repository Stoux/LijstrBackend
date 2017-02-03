package nl.lijstr.api.movies.models.post;

import java.math.BigDecimal;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import lombok.*;
import nl.lijstr.domain.movies.MovieRating;

/**
 * Created by Stoux on 20/04/2016.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieRatingRequest {

    private MovieRating.Seen seen;

    @DecimalMin(value = "1.0", message = "A rating can't be less than 1")
    @DecimalMax(value = "10.0", message = "A rating can't be more than 10")
    private BigDecimal rating;

    private String comment;

}
