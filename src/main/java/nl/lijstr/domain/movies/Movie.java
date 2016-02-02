package nl.lijstr.domain.movies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.*;
import lombok.*;
import nl.lijstr.domain.base.IdCmModel;
import nl.lijstr.domain.imdb.Genre;
import nl.lijstr.domain.imdb.Language;
import nl.lijstr.domain.movies.people.MovieActor;
import nl.lijstr.domain.movies.people.MovieDirector;
import nl.lijstr.domain.movies.people.MovieWriter;
import nl.lijstr.domain.users.User;
import nl.lijstr.services.modify.annotations.ModifiableWithHistory;
import nl.lijstr.services.modify.annotations.NotModifiable;

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
    @Column(unique = true)
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
    private List<Language> languages;

    //Relations | People
    @OneToMany(mappedBy = "movie")
    private List<MovieActor> actors;
    @OneToMany(mappedBy = "movie")
    private List<MovieWriter> writers;
    @OneToMany(mappedBy = "movie")
    private List<MovieDirector> directors;

}
