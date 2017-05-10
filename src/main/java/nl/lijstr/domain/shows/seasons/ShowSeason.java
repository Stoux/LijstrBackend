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

    private Long tvMazeId;

    private Integer seasonNumber;

    private boolean poster;

    @OneToMany(mappedBy = "season", cascade = CascadeType.PERSIST)
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

}
