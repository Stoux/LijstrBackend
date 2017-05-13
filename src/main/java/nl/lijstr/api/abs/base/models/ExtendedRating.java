package nl.lijstr.api.abs.base.models;

import java.time.LocalDateTime;
import lombok.*;
import nl.lijstr.api.movies.models.TimeBased;
import nl.lijstr.domain.base.RatingModel;

/**
 * A representable version of a {@link RatingModel}.
 */
@Getter
public class ExtendedRating extends ShortRating implements TimeBased {

    private long id;

    private LocalDateTime lastModified;
    private LocalDateTime created;

    private boolean latest;

    /**
     * Create a representable version of a {@link RatingModel}.
     *
     * @param rating The original rating
     */
    public ExtendedRating(RatingModel rating) {
        super(rating);
        this.id = rating.getId();

        this.lastModified = rating.getLastModified();
        this.created = rating.getCreated();

        this.latest = rating.getLatest();
    }
}
