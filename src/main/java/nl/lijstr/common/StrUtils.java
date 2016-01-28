package nl.lijstr.common;

import java.util.Collection;
import java.util.function.Function;

/**
 * Common String Utilities.
 */
public final class StrUtils {

    private StrUtils() {
    }

    /**
     * Convert a Collection to a single delimited string.
     *
     * @param collection     The list of items
     * @param toStringMethod The method to convert an item to a string
     * @param delimiter      The delimiter
     * @param <X>            Class of the item
     *
     * @return The combined string
     */
    public static <X> String collectionToDelimitedString(Collection<X> collection,
                                                         Function<X, String> toStringMethod,
                                                         String delimiter) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean added = false;
        for (X x : collection) {
            if (added) {
                stringBuilder.append(delimiter);
            }
            stringBuilder.append(toStringMethod.apply(x));
            added = true;
        }
        return stringBuilder.toString();
    }

    /**
     * Use a String if it's not null and not empty, otherwise use the default.
     * <p>
     * This is just a more readable version of:
     * isEmpty ? default : use
     *
     * @param use          The string
     * @param useAsDefault The default if use is empty
     *
     * @return the result
     */
    public static String useOrDefault(String use, String useAsDefault) {
        if (use == null || use.isEmpty()) {
            return useAsDefault;
        } else {
            return use;
        }
    }

}
