package nl.lijstr.api.movies.models.post;

import javax.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;

/**
 * Created by Leon Stam on 18-4-2016.
 */
@Getter
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class PostedMovieRequest {

    @NotEmpty
    @Pattern(regexp = "tt\\d{7}")
    private String imdbId;

    private String youtubeId;

}
