package nl.lijstr.api.movies.models;

import lombok.*;
import nl.lijstr.domain.movies.MovieRating;

/**
 * A summarized version of a {@link MovieRating}.
 */
@Getter
public class MovieShortRating {

    private boolean seen;
    private Double rating;
    private String comment;
    private long user;

    /**
     * Create a summarized version of a {@link MovieRating}.
     *
     * @param movieRating The original movie rating
     */
    public MovieShortRating(MovieRating movieRating) {
        this.seen = movieRating.isSeen();
        this.rating = movieRating.getRating();
        this.comment = movieRating.getComment();
        this.user = movieRating.getUser().getId();
    }
}
