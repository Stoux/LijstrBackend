package nl.lijstr.api.abs.base.models;

import java.math.BigDecimal;
import lombok.*;
import nl.lijstr.domain.base.RatingModel;
import nl.lijstr.domain.movies.MovieRating;

/**
 * A summarized version of a {@link MovieRating}.
 */
@Getter
public class ShortRating {

    private int seen;
    private BigDecimal rating;
    private String comment;
    private long user;

    /**
     * Create a summarized version of a {@link RatingModel}.
     *
     * @param rating The original rating
     */
    public ShortRating(RatingModel rating) {
        this.seen = rating.getSeen().ordinal();
        this.rating = rating.getRating();
        this.comment = rating.getComment();
        this.user = rating.getUser().getId();
    }

}
