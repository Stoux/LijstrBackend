package nl.lijstr.api.shows.models.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class PlaceCommentRequest {

    /**
     * Whether or not this comment should be flagged as a spoiler.
     */
    private boolean spoilers;
    /**
     * Should be a JSON string with operations.
     */
    private String comment;

}
