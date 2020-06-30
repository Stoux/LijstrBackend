package nl.lijstr.domain.shows;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import nl.lijstr.domain.base.IdCmModel;
import nl.lijstr.domain.shows.user.ShowSeasonComment;
import nl.lijstr.services.modify.annotations.ModifiableWithHistory;
import nl.lijstr.services.modify.annotations.NotModifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class for (TV) Show Seasons.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ModifiableWithHistory
public class ShowSeason extends IdCmModel {

    /** Sequential season number for specials */
    public static final int SPECIALS_SEQUENTIAL_ID = 0;

    @JsonBackReference
    @ManyToOne
    private Show show;

    // TMDB ID = Leading ID
    @Setter(value = AccessLevel.NONE)
    @NotModifiable
    @Column(unique = true, nullable = false)
    private Integer tmdbId;

    @Column(unique = true)
    private Integer tvdbId;

    // General info
    /** Sequential season number (0 = specials) */
    @Column(nullable = false)
    private Integer seasonNumber;
    private String title;
    @Lob private String overview;
    private String posterImage;

    // Relations
    @org.hibernate.annotations.OrderBy(clause = "episodeNumber ASC")
    @OneToMany(mappedBy = "season", cascade = CascadeType.ALL)
    private List<ShowEpisode> episodes;

    @OneToMany(mappedBy = "season")
    private List<ShowSeasonComment> comments;

    public ShowSeason(Show show, Integer tmdbId, Integer seasonNumber, String title, String overview, String posterImage) {
        this.show = show;
        this.tmdbId = tmdbId;
        this.seasonNumber = seasonNumber;
        this.title = title;
        this.overview = overview;
        this.posterImage = posterImage;
        this.episodes = new ArrayList<>();
    }

    /**
     * Whether or not this season is special episodes.
     * @return is specials
     */
    public boolean isSpecials() {
        return this.seasonNumber == SPECIALS_SEQUENTIAL_ID;
    }

}
