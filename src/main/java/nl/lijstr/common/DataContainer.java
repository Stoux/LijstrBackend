package nl.lijstr.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A Container class to hold a certain item.
 */
@Getter
@AllArgsConstructor
public class DataContainer<X> {

    private X data;

}
