package nl.lijstr.api.shows.models.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEpisodeMetaRequest {

    private boolean seen;
    private String reaction;




}
