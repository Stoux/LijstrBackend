package nl.lijstr.api.movies.models;

import lombok.Getter;
import nl.lijstr.domain.movies.MovieRating;

import java.math.BigDecimal;

/**
 * A summarized version of a {@link MovieRating}.
 */
@Getter
public class MovieShortRating {

    private int seen;
    private BigDecimal rating;
    private String comment;
    private long user;

    /**
     * Create a summarized version of a {@link MovieRating}.
     *
     * @param movieRating The original movie rating
     */
    public MovieShortRating(MovieRating movieRating) {
        this.seen = movieRating.getSeen().ordinal();
        this.rating = movieRating.getRating();
        this.comment = movieRating.getComment();
        this.user = movieRating.getUser().getId();
    }

}
