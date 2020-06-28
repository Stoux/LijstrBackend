package nl.lijstr.api.shows.models.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Pattern;

@Getter
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class PostedShowRequest {

    @Pattern(regexp = "tt\\d{7,8}")
    private String imdbId;

    private Integer tmdbId;

    @SuppressWarnings("unused")
    @AssertTrue
    public boolean hasAnId() {
        return imdbId != null || tmdbId != null;
    }

}
