package nl.lijstr.domain.shows.seasons;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.*;
import nl.lijstr.domain.shows.base.ShowBoundModel;
import nl.lijstr.domain.shows.episodes.ShowEpisode;

/**
 * A show's season.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ShowSeason extends ShowBoundModel {

    private Long tvMazeId;

    private Integer seasonNumber;

    private String plot;
    private boolean poster;

    @OneToMany
    private List<ShowEpisode> episodes;

    @OneToMany(mappedBy = "target")
    private List<ShowSeasonRating> seasonRatings;

}
