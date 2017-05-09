package nl.lijstr.domain.interfaces;

import java.time.LocalDateTime;

/**
 * Created by Stoux on 09/05/2017.
 */
public interface Target extends ImdbIdentifiable {

    /**
     * Get the target's title.
     *
     * @return the title
     */
    String getTitle();

    /**
     * Get the target's last updated value.
     *
     * @return timestamp of last update to this entity
     */
    LocalDateTime getLastUpdated();


}
