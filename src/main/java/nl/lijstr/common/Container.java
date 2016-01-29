package nl.lijstr.common;

import lombok.*;

/**
 * A Container class to hold a certain item.
 *
 * @param <X> The class of the item in the container
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Container<X> {

    private X item;

    /**
     * Check if the item is present.
     *
     * @return is present
     */
    public boolean isPresent() {
        return item != null;
    }

}
