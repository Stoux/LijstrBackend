package nl.lijstr.api.movies.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.*;
import nl.lijstr.common.StrUtils;
import nl.lijstr.common.Utils;
import nl.lijstr.domain.imdb.Genre;
import nl.lijstr.domain.imdb.SpokenLanguage;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieRating;

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

    private Integer runtime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String ageRating;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<Long, String> genres;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<Long, String> languages;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<Long, MovieShortRating> latestRatings;

    private BigDecimal averageUserRating;
    private Integer averageUserRatingCount;

    /**
     * Convert a Movie to a summarized version of itself.
     *
     * @param movie            The movie
     * @param useDutchTitle    Should include the dutch title (if available)
     * @param useOriginalTitle Should include the original title (if available)
     * @param includeGenres    Should include a map of genres
     * @param includeLanguages Should include a map of languages
     * @param includeAgeRating Should include the movie's age rating
     * @param requestUsers     Request the ratings of the given users (can be null for all that are available)
     *
     * @return the summary
     */
    public static MovieSummary convert(Movie movie,
                                       boolean useDutchTitle,
                                       boolean useOriginalTitle,
                                       boolean includeGenres,
                                       boolean includeLanguages,
                                       boolean includeAgeRating,
                                       Set<Long> requestUsers) {
        MovieSummaryBuilder builder = MovieSummary.builder()
            .id(movie.getId())
            .imdbId(movie.getImdbId())
            .year(movie.getYear())
            .runtime(movie.getRuntime())
            .imdbRating(movie.getImdbRating())
            .metacriticScore(movie.getMetacriticScore());

        if (useDutchTitle && movie.getDutchTitle() != null) {
            builder.title(movie.getDutchTitle());
        } else if (useOriginalTitle && movie.getOriginalTitle() != null) {
            builder.title(movie.getOriginalTitle());
        } else {
            builder.title(movie.getTitle());
        }

        if (requestUsers == null || !requestUsers.isEmpty()) {
            Stream<MovieRating> ratingStream = movie.getLatestMovieRatings().stream();
            if (requestUsers != null) {
                ratingStream = ratingStream.filter(r -> requestUsers.contains(r.getUser().getId()));
            }
            Map<Long, MovieShortRating> shortRatings = ratingStream
                .map(MovieShortRating::new)
                .collect(Collectors.toMap(MovieShortRating::getUser, o -> o));
            builder.latestRatings(shortRatings);

            // Calculate the average rating of all users
            BigDecimal average = new BigDecimal("0.0");
            int ratingCount = 0;
            for (MovieRating rating : movie.getLatestMovieRatings()) {
                if (rating.getRating() != null) {
                    average = average.add(rating.getRating());
                    ratingCount++;
                }
            }
            if (ratingCount >= 1) {
                builder.averageUserRating(
                    average.divide(new BigDecimal(ratingCount), RoundingMode.HALF_UP)
                );
                builder.averageUserRatingCount(ratingCount);
            }
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
