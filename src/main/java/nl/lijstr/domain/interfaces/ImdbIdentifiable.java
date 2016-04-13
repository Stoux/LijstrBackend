package nl.lijstr.domain.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This object has an IMDB ID that it can be identified with.
 */
public interface ImdbIdentifiable {

    /**
     * Get the IMDB ID.
     *
     * @return the ID
     */
    @JsonIgnore
    String getImdbId();

}
