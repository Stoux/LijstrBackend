package nl.lijstr.api.movies.models;

import java.time.LocalDateTime;
import lombok.Getter;
import nl.lijstr.domain.movies.MovieRating;

/**
 * A representable version of a {@link MovieRating}.
 */
@Getter
public class MovieExtendedRating extends MovieShortRating implements TimeBased {

    private long id;

    private LocalDateTime lastModified;
    private LocalDateTime created;

    private boolean latest;

    /**
     * Create a representable version of a {@link MovieRating}.
     *
     * @param movieRating The original movie rating
     */
    public MovieExtendedRating(MovieRating movieRating) {
        super(movieRating);
        this.id = movieRating.getId();

        this.lastModified = movieRating.getLastModified();
        this.created = movieRating.getCreated();

        this.latest = movieRating.getLatest();
    }
}
