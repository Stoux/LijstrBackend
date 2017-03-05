package nl.lijstr.common;

import lombok.*;

/**
 * A Container class to hold a certain item.
 */
@Getter
@AllArgsConstructor
public class DataContainer<X> {

    private X data;

}
