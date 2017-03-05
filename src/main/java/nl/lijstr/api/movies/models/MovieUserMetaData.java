package nl.lijstr.api.movies.models;

import lombok.*;
import nl.lijstr.domain.movies.MovieUserMeta;

/**
 * A representable version of a {@link MovieUserMeta} object.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MovieUserMetaData {


    private boolean wantToWatch;


    /**
     * Update a domain object with the values from this data object.
     *
     * @param toUpdate The domain object
     */
    public void update(MovieUserMeta toUpdate) {
        toUpdate.setWantToWatch(wantToWatch);
    }


    /**
     * Create a data object from it's domain object.
     *
     * @param meta The domain object
     *
     * @return the data object
     */
    public static MovieUserMetaData fromDomain(MovieUserMeta meta) {
        return new MovieUserMetaData(meta.isWantToWatch());
    }

    /**
     * Create a meta data object with default values.
     *
     * @return the data object
     */
    public static MovieUserMetaData withDefaultValues() {
        return new MovieUserMetaData(false);
    }


}
