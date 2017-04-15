package nl.lijstr.domain.shows.episodes;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.*;
import nl.lijstr.domain.base.IdCmModel;
import nl.lijstr.domain.shows.seasons.ShowSeason;

/**
 * A show's episode.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ShowEpisode extends IdCmModel {

    private String imdbId;
    private Long tvMazeId;

    @ManyToOne
    private ShowSeason season;
    private Integer episodeNumber;

    private LocalDate airDate;

    private String plot;
    private Integer runtime;

    private Double imdbRating;
    private Long imdbVotes;

}
