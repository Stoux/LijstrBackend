package nl.lijstr.api.movies.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.lijstr.common.Utils;
import nl.lijstr.domain.imdb.Genre;
import nl.lijstr.domain.imdb.SpokenLanguage;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieRating;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * A different model based on a {@link Movie}.
 */
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MovieDetail {

    private long id;

    private LocalDateTime created;
    private LocalDateTime lastUpdated;

    private String imdbId;
    private String title;

    private Integer year;
    private LocalDate released;

    private Double imdbRating;
    private Long imdbVotes;
    private Integer metacriticScore;

    private String shortPlot;
    private String longPlot;

    private String ageRating;
    private boolean hasPoster;

    private String youtubeUrl;

    private Long addedBy;

    private Set<Long> genres;
    private Set<Long> languages;

    private List<MovieRating> latestMovieRatings;

    /**
     * Convert a {@link Movie} to a {@link MovieDetail} object.
     *
     * @param movie The movie
     * @return the detail object
     */
    public static MovieDetail fromMovie(Movie movie) {
        MovieDetailBuilder builder = MovieDetail.builder()
                .id(movie.getId())
                .created(movie.getCreated())
                .lastUpdated(movie.getLastUpdated())
                .imdbId(movie.getImdbId())
                .title(movie.getTitle())
                .year(movie.getYear())
                .released(movie.getReleased())
                .imdbRating(movie.getImdbRating())
                .imdbVotes(movie.getImdbVotes())
                .metacriticScore(movie.getMetacriticScore())
                .shortPlot(movie.getShortPlot())
                .longPlot(movie.getLongPlot())
                .ageRating(movie.getAgeRating())
                .hasPoster(movie.isPoster())
                .youtubeUrl(movie.getYoutubeUrl())
                .genres(Utils.toSet(movie.getGenres(), Genre::getId))
                .languages(Utils.toSet(movie.getLanguages(), SpokenLanguage::getId));

        if (movie.getAddedBy() != null) {
            builder.addedBy(movie.getAddedBy().getId());
        }

        return builder.build();
    }

}
