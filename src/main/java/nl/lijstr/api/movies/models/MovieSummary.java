package nl.lijstr.api.movies.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MovieSummary {

    private long id;
    private String imdbId;
    private String title;
    private int year;

    private Double imdbRating;
    private Integer metacriticScore;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String ageRating;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<Long, String> genres;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<Long, String> languages;

    private Map<Long, MovieShortRating> latestRatings;

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
                                       boolean useDutchTitle,
                                       boolean useOriginalTitle,
                                       boolean includeGenres,
                                       boolean includeLanguages,
                                       boolean includeAgeRating) {
        Map<Long, MovieShortRating> shortRatings = movie.getLatestMovieRatings().stream()
                .map(MovieShortRating::new)
                .collect(Collectors.toMap(MovieShortRating::getUser, o -> o));

        MovieSummaryBuilder builder = MovieSummary.builder()
                .id(movie.getId())
                .imdbId(movie.getImdbId())
                .year(movie.getYear())
                .imdbRating(movie.getImdbRating())
                .metacriticScore(movie.getMetacriticScore())
                .latestRatings(shortRatings);

        if (useDutchTitle && movie.getDutchTitle() != null) {
            builder.title(movie.getDutchTitle());
        } else if (useOriginalTitle && movie.getOriginalTitle() != null) {
            builder.title(movie.getOriginalTitle());
        } else {
            builder.title(movie.getTitle());
        }


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
