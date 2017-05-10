package nl.lijstr.domain.interfaces;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.*;
import nl.lijstr.domain.base.IdCmModel;
import nl.lijstr.domain.imdb.Genre;
import nl.lijstr.domain.imdb.SpokenLanguage;
import nl.lijstr.domain.users.User;
import nl.lijstr.services.modify.annotations.NotModifiable;

/**
 * Base entity class that contains shared attributes.
 */
@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class Target extends IdCmModel implements ImdbIdentifiable {

    @Setter(value = AccessLevel.NONE)
    @NotModifiable
    @Column(unique = true, nullable = false)
    private String imdbId;

    private String title;
    private String originalTitle;
    private String dutchTitle;

    private Double imdbRating;
    private Long imdbVotes;
    private Integer metacriticScore;

    @Lob
    private String shortPlot;
    @Lob
    private String longPlot;

    private Integer runtime;

    //Rated for X
    private String ageRating;

    //Has poster
    private boolean poster;

    private String youtubeUrl;

    @NotModifiable
    private LocalDateTime lastUpdated;

    @ManyToOne
    private User addedBy;

    private Long oldSiteId;

    //Details
    @ManyToMany(cascade = CascadeType.PERSIST)
    private List<Genre> genres;
    @ManyToMany(cascade = CascadeType.PERSIST)
    private List<SpokenLanguage> languages;

    protected Target(String imdbId) {
        this.imdbId = imdbId;
        this.genres = new ArrayList<>();
        this.languages = new ArrayList<>();
    }

    public abstract List<?> getTrivia();

}
