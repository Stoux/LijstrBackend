package nl.lijstr.domain.shows.meta;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Describes all possible seen states for a {@link nl.lijstr.domain.shows.Show}.
 */
public enum ShowSeen {

    /** User has specified that they have seen this show */
    YES,
    /** User has specified that they haven't seen this show */
    NO,
    /** User has specified that they have seen episodes in an irregular fashion. Unsure which episodes */
    TV,
    /** User is tracking watch status of episodes */
    TRACKED,
    /** User is not sure if they have seen this show */
    UNKNOWN;

    @JsonValue
    public String toValue() {
        return name();
    }

}
