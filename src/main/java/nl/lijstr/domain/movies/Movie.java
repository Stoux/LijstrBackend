package nl.lijstr.domain.movies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.*;
import lombok.*;
import nl.lijstr.domain.base.IdCmModel;
import nl.lijstr.domain.imdb.Genre;
import nl.lijstr.domain.imdb.SpokenLanguage;
import nl.lijstr.domain.movies.people.MovieCharacter;
import nl.lijstr.domain.movies.people.MovieDirector;
import nl.lijstr.domain.movies.people.MovieWriter;
import nl.lijstr.domain.users.User;
import nl.lijstr.services.modify.annotations.ModifiableWithHistory;
import nl.lijstr.services.modify.annotations.NotModifiable;
import org.hibernate.annotations.Where;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ModifiableWithHistory
public class Movie extends IdCmModel {

    @Setter(value = AccessLevel.NONE)
    @NotModifiable
    @Column(unique = true, nullable = false)
    private String imdbId;

    private String title;
    @JsonIgnore
    @NotModifiable
    private String originalTitle;

    private Integer year;
    private LocalDate released;

    private Double imdbRating;
    private Long imdbVotes;
    private Integer metacriticScore;

    @Lob
    private String shortPlot;
    @Lob
    private String longPlot;

    //TODO: Runtime (1 or versions?)

    //Rated for X
    private String ageRating;

    //Has poster
    private boolean poster;

    private String youtubeUrl;

    @NotModifiable
    private LocalDateTime lastUpdated;

    @ManyToOne
    private User addedBy;

    //Relations | Details
    @ManyToMany(cascade = CascadeType.PERSIST)
    private List<Genre> genres;
    @ManyToMany(cascade = CascadeType.PERSIST)
    private List<SpokenLanguage> languages;

    //Relations | People
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private List<MovieCharacter> characters;
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private List<MovieWriter> writers;
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private List<MovieDirector> directors;

    //Relations | External items
    @JsonIgnore
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieTrivia> trivia;

    @OneToMany(mappedBy = "movie")
    private List<MovieComment> movieComments;

    @Where(clause = "latest='1'")
    @OneToMany(mappedBy = "movie")
    private List<MovieRating> latestMovieRatings;

    @OneToMany(mappedBy = "movie")
    private List<MovieRating> movieRatings;

    @JsonIgnore
    @OneToMany(mappedBy = "movie")
    private List<MovieUserMeta> movieUserMetaList;

    /**
     * Create a new Movie by it's IMDB ID.
     *
     * @param imdbId The ID
     */
    public Movie(String imdbId) {
        this.imdbId = imdbId;
    }

    /**
     * Create a new Movie by it's IMDB ID.
     *
     * @param imdbId     The ID
     * @param youtubeUrl The YouTube trailer URL
     * @param addedBy    The user who added this movie
     */
    public Movie(String imdbId, String youtubeUrl, User addedBy) {
        this.imdbId = imdbId;
        this.youtubeUrl = youtubeUrl;
        this.addedBy = addedBy;
    }

}
