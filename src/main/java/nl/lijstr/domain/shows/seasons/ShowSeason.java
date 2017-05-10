package nl.lijstr.domain.shows.seasons;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.*;
import nl.lijstr.domain.shows.Show;
import nl.lijstr.domain.shows.base.ShowBoundModel;
import nl.lijstr.domain.shows.episodes.ShowEpisode;

/**
 * A show's season.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class ShowSeason extends ShowBoundModel {

    @Setter(AccessLevel.NONE)
    private Long tvMazeId;

    private Integer seasonNumber;

    private String title;
    private boolean poster;

    @OneToMany(mappedBy = "season", cascade = CascadeType.ALL)
    private List<ShowEpisode> episodes;

    @OneToMany(mappedBy = "target")
    private List<ShowSeasonRating> seasonRatings;

    /**
     * Create a new {@link ShowSeason}.
     *
     * @param show         The show
     * @param seasonNumber The season number
     */
    public ShowSeason(Show show, Integer seasonNumber) {
        super(show);
        this.seasonNumber = seasonNumber;
        this.poster = false;
        this.episodes = new ArrayList<>();
        this.seasonRatings = new ArrayList<>();
    }

    /**
     * Create a new {@link ShowSeason} that's linked to TvMaze.
     *
     * @param show         The show
     * @param tvMazeId     The TvMaze ID
     * @param seasonNumber The season number
     */
    public ShowSeason(Show show, Long tvMazeId, Integer seasonNumber) {
        this(show, seasonNumber);
        this.tvMazeId = tvMazeId;
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
