package nl.lijstr.api.movies.models.post;

import javax.persistence.Column;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;

/**
 * Created by Stoux on 20-7-2016.
 */
@Getter
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class PostedMovieComment {

    @NotEmpty
    @Column(length = 5000)
    private String comment;

}
