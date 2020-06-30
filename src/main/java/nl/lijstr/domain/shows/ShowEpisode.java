package nl.lijstr.domain.shows;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import nl.lijstr.domain.base.IdCmModel;
import nl.lijstr.domain.shows.user.ShowEpisodeComment;
import nl.lijstr.domain.shows.user.ShowEpisodeUserMeta;
import nl.lijstr.services.modify.annotations.ModifiableWithHistory;
import nl.lijstr.services.modify.annotations.NotModifiable;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Model class for (TV) Show Episodes.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ModifiableWithHistory
public class ShowEpisode extends IdCmModel {

    @JsonBackReference
    @ManyToOne
    private Show show;

    @JsonBackReference
    @ManyToOne
    private ShowSeason season;

    // IDs
    // TMDB ID = Leading ID
    @Setter(value = AccessLevel.NONE)
    @NotModifiable
    @Column(unique = true, nullable = false)
    private Integer tmdbId;

    @Column(unique = true)
    private String imdbId;
    @Column(unique = true)
    private Integer tvdbId;

    /** Date that this episode aired for the first time */
    private LocalDate airDate;
    /** Sequential episode number of this season */
    private Integer episodeNumber;

    // General info
    private String title;
    @Lob private String overview;
    private String productionCode;
    private String screenshotImage;

    // Scores
    private Double tmdbRating;
    private Integer tmdbVotes;

    // Relations
    @JsonIgnore
    @OneToMany(mappedBy = "episode")
    private List<ShowEpisodeUserMeta> userMetas;

    @OneToMany(mappedBy = "episode")
    private List<ShowEpisodeComment> comments;

    public ShowEpisode(Show show, ShowSeason season, Integer tmdbId) {
        this.show = show;
        this.season = season;
        this.tmdbId = tmdbId;
    }
}
