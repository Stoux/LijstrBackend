package nl.lijstr.domain.shows;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.*;
import lombok.*;
import nl.lijstr.domain.base.IdCmModel;
import nl.lijstr.domain.imdb.Genre;
import nl.lijstr.domain.imdb.SpokenLanguage;
import nl.lijstr.domain.shows.people.ShowCharacter;
import nl.lijstr.domain.shows.people.ShowDirector;
import nl.lijstr.domain.shows.people.ShowWriter;
import nl.lijstr.domain.shows.seasons.ShowSeason;
import nl.lijstr.domain.users.User;
import nl.lijstr.services.modify.annotations.ModifiableWithHistory;
import nl.lijstr.services.modify.annotations.NotModifiable;
import org.hibernate.annotations.Where;

/**
 * A (TV-)show / series.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ModifiableWithHistory
public class Show extends IdCmModel {

    @Setter(value = AccessLevel.NONE)
    @NotModifiable
    @Column(unique = true, nullable = false)
    private String imdbId;

    @NotModifiable
    @Column(unique = true)
    private String tmdbId;

    @NotModifiable
    @Column(unique = true)
    private Long tvMazeId;

    private String title;
    private String originalTitle;
    private String dutchTitle;

    @Lob
    private String shortPlot;
    @Lob
    private String longPlot;

    private String status; //eg 'Running'
    private String scriptType; //Eg 'Reality'/'Scripted'

    private Integer startYear;
    private Integer endYear;
    private LocalDate premiereDate;

    private Double imdbRating;
    private Long imdbVotes;
    private Integer metacriticScore;

    private String ageRating;
    private Integer runtime;
    private boolean poster;

    @NotModifiable
    private LocalDateTime lastUpdated;

    @ManyToOne
    private User addedBy;

    private Long oldSiteId;

    //Relations | Children
    @OneToMany
    private List<ShowSeason> seasons;

    //Relations | Details
    @ManyToMany(cascade = CascadeType.PERSIST)
    private List<Genre> genres;
    @ManyToMany(cascade = CascadeType.PERSIST)
    private List<SpokenLanguage> languages;

    //Relations | People
    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL)
    private List<ShowCharacter> characters;
    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL)
    private List<ShowWriter> writers;
    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL)
    private List<ShowDirector> directors;

    //Relations | External items
    @JsonIgnore
    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShowTrivia> trivia;

    @Where(clause = "latest='1'")
    @OneToMany(mappedBy = "target")
    private List<ShowRating> latestShowRatings;

    @OneToMany(mappedBy = "target")
    private List<ShowRating> showRatings;

}
