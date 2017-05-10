package nl.lijstr.domain.shows;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.*;
import nl.lijstr.domain.imdb.Genre;
import nl.lijstr.domain.imdb.SpokenLanguage;
import nl.lijstr.domain.interfaces.Target;
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
public class Show extends Target {

    @Setter(value = AccessLevel.NONE)
    @NotModifiable
    @Column(unique = true)
    private String tmdbId;

    @Setter(value = AccessLevel.NONE)
    @NotModifiable
    @Column(unique = true)
    private Long tvMazeId;

    private String status; //eg 'Running'
    private String scriptType; //Eg 'Reality'/'Scripted'

    private Integer startYear;
    private Integer endYear;
    private LocalDate premiereDate;

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

    /**
     * Create a new Show by it's IMDB ID.
     *
     * @param imdbId     The ID
     * @param title      The title
     * @param youtubeUrl The YouTube trailer URL
     * @param addedBy    The user who added this movie
     */
    public Show(String imdbId, String title, String youtubeUrl, User addedBy) {
        super(imdbId);
        setTitle(title);
        setYoutubeUrl(youtubeUrl);
        setAddedBy(addedBy);

        this.seasons = new ArrayList<>();
        this.genres = new ArrayList<>();
        this.languages = new ArrayList<>();
        this.characters = new ArrayList<>();
        this.writers = new ArrayList<>();
        this.directors = new ArrayList<>();
        this.trivia = new ArrayList<>();
        this.latestShowRatings = new ArrayList<>();
        this.showRatings = new ArrayList<>();
    }

    /**
     * Set the TheMovieDatabase ID.
     * <p>
     * Warning: This can only be done once (while the ID is still null).
     *
     * @param tmdbId The ID
     */
    public void setTmdbId(String tmdbId) {
        if (this.tmdbId != null) {
            throw new IllegalStateException("TheMovieDB ID is already set");
        }
        this.tmdbId = tmdbId;
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
