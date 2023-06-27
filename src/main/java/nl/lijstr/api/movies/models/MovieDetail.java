package nl.lijstr.api.movies.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.lijstr.domain.imdb.Genre;
import nl.lijstr.domain.imdb.Person;
import nl.lijstr.domain.imdb.SpokenLanguage;
import nl.lijstr.domain.interfaces.PersonBound;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieCollection;
import nl.lijstr.domain.movies.MovieRating;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A different model based on a {@link Movie}.
 */
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MovieDetail {

    private long id;
    private Long oldSiteId;

    private LocalDateTime created;
    private LocalDateTime lastUpdated;

    private String imdbId;
    private String title;
    private String originalTitle;
    private String dutchTitle;

    private Integer year;
    private LocalDate released;
    private Integer runtime;

    private Double imdbRating;
    private Long imdbVotes;
    private Integer metacriticScore;

    private String shortPlot;
    private String longPlot;

    private String ageRating;
    private boolean hasPoster;

    private String youtubeUrl;

    private Long addedBy;

    private List<Genre> genres;
    private List<SpokenLanguage> languages;

    private List<MovieCollection> collections;

    private List<Person> writers;
    private List<Person> directors;

    private List<MovieShortRating> latestMovieRatings;

    /**
     * Convert a {@link Movie} to a {@link MovieDetail} object.
     *
     * @param movie The movie
     *
     * @return the detail object
     */
    public static MovieDetail fromMovie(Movie movie) {
        MovieDetailBuilder builder = MovieDetail.builder()
                .id(movie.getId())
                .oldSiteId(movie.getOldSiteId())
                .created(movie.getCreated())
                .lastUpdated(movie.getLastUpdated())
                .imdbId(movie.getImdbId())
                .title(movie.getTitle())
                .originalTitle(movie.getOriginalTitle())
                .dutchTitle(movie.getDutchTitle())
                .year(movie.getYear())
                .released(movie.getReleased())
                .runtime(movie.getRuntime())
                .imdbRating(movie.getImdbRating())
                .imdbVotes(movie.getImdbVotes())
                .metacriticScore(movie.getMetacriticScore())
                .shortPlot(movie.getShortPlot())
                .longPlot(movie.getLongPlot())
                .ageRating(movie.getAgeRating())
                .hasPoster(movie.isPoster())
                .youtubeUrl(movie.getYoutubeUrl())
                .collections(movie.getCollections())
                .genres(movie.getGenres())
                .languages(movie.getLanguages());

        List<MovieRating> ratings = movie.getLatestMovieRatings();
        if (ratings != null) {
            List<MovieShortRating> shortRatings = ratings.stream().map(MovieShortRating::new)
                    .collect(Collectors.toList());
            builder.latestMovieRatings(shortRatings);
        }

        builder.writers(mapPersonBound(movie.getWriters()));
        builder.directors(mapPersonBound(movie.getDirectors()));

        if (movie.getAddedBy() != null) {
            builder.addedBy(movie.getAddedBy().getId());
        }

        return builder.build();
    }

    private static List<Person> mapPersonBound(Collection<? extends PersonBound> personBoundCollection) {
        return personBoundCollection.stream()
            .map(PersonBound::getPerson)
            .collect(Collectors.toList());
    }

}
