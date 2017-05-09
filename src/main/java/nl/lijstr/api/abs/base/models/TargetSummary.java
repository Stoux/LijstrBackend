package nl.lijstr.api.abs.base.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import lombok.*;

/**
 * A summary object regarding a {@link nl.lijstr.domain.interfaces.Target} item.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class TargetSummary {

    protected long id;
    private String imdbId;
    private String title;

    private Double imdbRating;
    private Integer metacriticScore;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<Long, ShortRating> latestRatings;

}
