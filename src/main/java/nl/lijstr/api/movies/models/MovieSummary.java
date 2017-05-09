package nl.lijstr.api.movies.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.*;
import nl.lijstr.api.abs.base.models.ShortRating;
import nl.lijstr.api.abs.base.models.TargetSummary;
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
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MovieSummary extends TargetSummary {

    private int year;
    private Integer runtime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String ageRating;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<Long, String> genres;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<Long, String> languages;

    @Builder
    private MovieSummary(long id, String imdbId, String title, Double imdbRating, Integer metacriticScore,
                        Map<Long, ShortRating> latestRatings, int year, Integer runtime, String ageRating,
                        Map<Long, String> genres, Map<Long, String> languages) {
        super(id, imdbId, title, imdbRating, metacriticScore, latestRatings);
        this.year = year;
        this.runtime = runtime;
        this.ageRating = ageRating;
        this.genres = genres;
        this.languages = languages;
    }

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
            Map<Long, ShortRating> shortRatings = ratingStream
                .map(ShortRating::new)
                .collect(Collectors.toMap(ShortRating::getUser, o -> o));
            builder.latestRatings(shortRatings);
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
