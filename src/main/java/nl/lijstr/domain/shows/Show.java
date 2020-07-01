package nl.lijstr.domain.shows;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import nl.lijstr.domain.base.IdCmModel;
import nl.lijstr.domain.shows.user.ShowRating;
import nl.lijstr.domain.users.User;
import nl.lijstr.services.modify.annotations.ModifiableWithHistory;
import nl.lijstr.services.modify.annotations.NotModifiable;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class for (TV) Shows.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ModifiableWithHistory
public class Show extends IdCmModel {

    // TMDB ID = Leading ID
    @Setter(value = AccessLevel.NONE)
    @NotModifiable
    @Column(unique = true, nullable = false)
    private Integer tmdbId;

    @Column(unique = true)
    private String imdbId;
    @Column(unique = true)
    private Integer tvdbId;

    // General info
    private String title;
    private String originalTitle;
    private String dutchTitle;

    @Lob
    private String overview;

//    private LocalDate firstAirDate;
//    private LocalDate nextAirDate;

    // Scores
    private Double tmdbRating;
    private Integer tmdbVotes;
    private Double imdbRating;
    private Long imdbVotes;
    private Double metacriticScore;
    /** How popular this show is on TMDB in percentages (i.e. 94.35%) */
    private Double tmdbPopularity;

    /** Status of this series, i.e. 'Ended', 'Running' */
    private String status;
    /** Show type, i.e. 'Scripted', 'Reality TV' */
    private String type;
    /** Is currently in production / development */
    private Boolean inProduction;

    // Images
    private String backdropImage;
    private String posterImage;

    @NotModifiable
    private LocalDateTime lastUpdated;

    // Relations
    @JsonIgnore
    @Where(clause = "season_number>=1")
    @org.hibernate.annotations.OrderBy(clause = "seasonNumber ASC")
    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL)
    private List<ShowSeason> seasons;

    @org.hibernate.annotations.OrderBy(clause = "seasonNumber ASC")
    @OneToMany(mappedBy = "show")
    private List<ShowSeason> seasonsIncludingSpecials;

    @JsonIgnore
    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL)
    private List<ShowEpisode> episodes;

    @ManyToOne
    private User addedBy;

    @OneToMany(mappedBy = "show")
    private List<ShowRating> showRatings;

    public Show(int tmdbId) {
        this.tmdbId = tmdbId;
        this.seasons = new ArrayList<>();
        this.seasonsIncludingSpecials = new ArrayList<>();
        this.episodes = new ArrayList<>();
        this.showRatings = new ArrayList<>();
    }

    public Show(int tmdbId, User addedBy) {
        this(tmdbId);
        this.addedBy = addedBy;
    }


}
