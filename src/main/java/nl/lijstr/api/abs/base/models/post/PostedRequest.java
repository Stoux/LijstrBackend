package nl.lijstr.api.abs.base.models.post;

import javax.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;

/**
 * A model to request a new IMDB entity.
 */
@Getter
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class PostedRequest {

    @NotEmpty
    @Pattern(regexp = "tt\\d{7}")
    private String imdbId;

    private String youtubeId;

}
