package nl.lijstr.services.maf.handlers.util;

import java.time.DateTimeException;
import java.time.LocalDate;

/**
 * Field Converters for {@link nl.lijstr.domain.movies.Movie} and {@link nl.lijstr.services.maf.models.ApiMovie}.
 * NOTE: None of these support negative numbers.
 */
@SuppressWarnings("squid:S109")
public final class FieldConverters {

    private FieldConverters() {

    }

    /**
     * Convert a string to a LocalDate.
     * Expected format: yyyyMMdd
     *
     * @param s The string
     *
     * @return the date or null
     */
    public static LocalDate convertToDate(String s) {
        int sLength = s.length();
        if (!allNumbers(s) || sLength < 4) {
            return null;
        } else {
            int day = 1;
            int month = 1;
            int year = Integer.valueOf(s.substring(0, 4));

            if (sLength >= 6) {
                month = Integer.valueOf(s.substring(4, 6));
            }
            if (sLength >= 8) {
                day = Integer.valueOf(s.substring(6, 8));
            }

            try {
                return LocalDate.of(year, month, day);
            } catch (DateTimeException e) {
                return null;
            }
        }
    }

    /**
     * Convert a string to year.
     * Expects a 4 digit long string, all numbers.
     *
     * @param s The string
     *
     * @return The year or null
     */
    public static Integer convertToYear(String s) {
        return asInt(s, 4);
    }

    /**
     * Convert a string to a double.
     * <p>
     * NOTICE: This does support both .'s and ,'s as separator.
     * This means it doesn't support thousand separators.
     * It also supports integers that will be converted to doubles.
     *
     * @param s The string
     *
     * @return the double or null
     */
    public static Double convertToDouble(String s) {
        String sanitized = s.replace(" ", "").replace(",", ".");
        if (sanitized.matches("^[0-9]+\\.[0-9]+$")) {
            return Double.parseDouble(sanitized);
        } else if (allNumbers(sanitized)) {
            return 0.0 + Integer.parseInt(sanitized);
        } else {
            return null;
        }
    }

    /**
     * Convert a string to a Long.
     * This supports both ,'s as .'s as thousand separators.
     *
     * @param s The string
     *
     * @return the long or null
     */
    public static Long convertToLong(String s) {
        String sanitized = s.replace(",", "").replace(".", "");
        if (allNumbers(sanitized)) {
            return Long.parseLong(sanitized);
        } else {
            return null;
        }
    }

    /**
     * Convert a string to an Integer by assuming MetaCritic notation.
     * Expected format xx, x/100 or xx/100.
     *
     * @param s The string
     *
     * @return The int (x) or null
     */
    public static Integer convertMetaCriticScore(String s) {
        if (s.matches("^[0-9]{1,3}/100$")) {
            String[] split = s.split("/");
            int result = Integer.parseInt(split[0]);
            if (result > 100) {
                return null;
            } else {
                return result;
            }
        } else if (s.matches("^[0-9]{1,2}$")) {
            return Integer.parseInt(s);
        } else {
            return null;
        }
    }

    private static boolean allNumbers(String s) {
        return s.matches("^[0-9]+$");
    }

    private static Integer asInt(String s, int expectedLength) {
        if (expectedLength != s.length()) {
            return null;
        }
        if (!allNumbers(s)) {
            return null;
        }
        return Integer.parseInt(s);
    }


}
