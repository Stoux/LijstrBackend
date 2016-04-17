package nl.lijstr.api.movies.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.*;
import nl.lijstr.common.StrUtils;
import nl.lijstr.common.Utils;
import nl.lijstr.domain.imdb.Genre;
import nl.lijstr.domain.imdb.SpokenLanguage;
import nl.lijstr.domain.movies.Movie;

/**
 * A summarized version of a {@link Movie}.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MovieSummary {

    @JsonIgnore
    private long id;
    private String imdbId;
    private String title;
    private int year;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String ageRating;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<Long, String> genres;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<Long, String> languages;

    private List<MovieShortRating> latestRatings;

    /**
     * Convert a Movie to a summarized version.
     *
     * @param movie            The movie
     * @param includeGenres    Should include a map of genres
     * @param includeLanguages Should include a map of languages
     * @param includeAgeRating Should include the movie's age rating
     *
     * @return the summary
     */
    public static MovieSummary convert(Movie movie,
                                       boolean includeGenres,
                                       boolean includeLanguages,
                                       boolean includeAgeRating) {
        List<MovieShortRating> shortRatings = movie.getLatestMovieRatings().stream()
                .map(MovieShortRating::new)
                .collect(Collectors.toList());

        MovieSummaryBuilder builder = MovieSummary.builder()
                .id(movie.getId())
                .imdbId(movie.getImdbId())
                .title(movie.getTitle())
                .year(movie.getYear())
                .latestRatings(shortRatings);

        if (includeAgeRating) {
            builder.ageRating(StrUtils.useOrDefault(movie.getAgeRating(), "N/A"));
        }

        if (includeGenres) {
            builder.genres(Utils.toMap(movie.getGenres(), Genre::getId, Genre::getGenre));
        }
        if (includeLanguages) {
            builder.languages(Utils.toMap(movie.getLanguages(), SpokenLanguage::getId, SpokenLanguage::getLanguage));
        }

        return builder.build();
    }

}
