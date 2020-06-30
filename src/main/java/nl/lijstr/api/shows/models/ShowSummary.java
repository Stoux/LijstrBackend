package nl.lijstr.api.shows.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * A summarized version of a {@link nl.lijstr.domain.shows.Show}
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ShowSummary {

    private Long id;
    private String title;
    // Year of first release
    private Integer startYear;
    // Year of last episode (if not still in production)
    private Integer endYear;

    // Date of the next episode that's going to air (if any)
    private LocalDate nextEpisode;

    // Total number of seasons
    private Integer seasons;
    // Total number of episodes
    private Integer episodes;

    private Double imdbRating;
    private Double tmdbRating;

    private String status;
    private String type;

    // TODO: Ratings


}
