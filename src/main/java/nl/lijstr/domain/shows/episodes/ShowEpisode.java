package nl.lijstr.domain.shows.episodes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import javax.persistence.*;
import lombok.*;
import nl.lijstr.domain.base.IdCmModel;
import nl.lijstr.domain.other.DateAccuracy;
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

    @Setter(AccessLevel.NONE)
    private String imdbId;
    @Setter(AccessLevel.NONE)
    private Long tvMazeId;

    @ManyToOne
    private ShowSeason season;
    @Column(nullable = false)
    private Integer episodeNumber;

    private LocalDateTime airDate;
    @Enumerated(EnumType.STRING)
    private DateAccuracy airDateAccuracy;

    private String title;
    @Lob
    private String plot;
    private Integer runtime;

    private Double imdbRating;
    private Long imdbVotes;

    @OneToMany(mappedBy = "target")
    private List<ShowEpisodeRating> episodeRatings;

    public ShowEpisode(String imdbId, ShowSeason season, Integer episodeNumber) {
        this.imdbId = imdbId;
        this.season = season;
        this.episodeNumber = episodeNumber;
    }

    public ShowEpisode(Long tvMazeId, ShowSeason season, Integer episodeNumber) {
        this.tvMazeId = tvMazeId;
        this.season = season;
        this.episodeNumber = episodeNumber;
    }

    /**
     * Set the IMDB ID.
     * <p>
     * Warning: This can only be done once (while the ID is still null).
     *
     * @param imdbId The ID
     */
    public void setImdbId(String imdbId) {
        if (this.imdbId != null) {
            throw new IllegalStateException("IMDB ID is already set");
        }
        this.imdbId = imdbId;
    }

    /**
     * Set the TV Maze ID.
     * <p>
     * Warning: This can only be done once (while the ID is still null).
     *
     * @param tvMazeId The ID
     */
    public void setTvMazeId(Long tvMazeId) {
        if (this.tvMazeId != null) {
            throw new IllegalStateException("TV Maze ID is already set");
        }
        this.tvMazeId = tvMazeId;
    }

}
