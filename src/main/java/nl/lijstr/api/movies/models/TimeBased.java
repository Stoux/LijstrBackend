package nl.lijstr.api.movies.models;

import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * Specifies an object that has timestamps and can be sorted based on those stamps.
 * Default compare implementation is based on the created value.
 * See {@link #lastModifiedComparator()} for alternative comparator.
 */
public interface TimeBased extends Comparable<TimeBased> {

    /**
     * Get the created timestamp.
     *
     * @return the timestamp
     */
    LocalDateTime getCreated();

    /**
     * Get the last modified timestamp.
     *
     * @return the timestamp
     */
    LocalDateTime getLastModified();

    @Override
    default int compareTo(TimeBased o) {
        //Reverse order as newest should be first
        return o.getCreated().compareTo(getCreated());
    }

    /**
     * Get the comperator for the last modified field.
     *
     * @return a comparator
     */
    static Comparator<TimeBased> lastModifiedComparator() {
        //Reverse order as newest should be first
        return Comparator.comparing(TimeBased::getLastModified).reversed();
    }

}
