package nl.lijstr.services.maf.models;

import lombok.Getter;

/**
 * Different name a movie might be called (in another country).
 */
@Getter
public class ApiAka {

    private String country;
    private String title;
    private String comment;

    /**
     * Check if the country is dutch (Netherlands, Nederland, Dutch)
     *
     * @return is dutch
     */
    public boolean isDutch() {
        if (country == null) {
            return false;
        }
        String lc = country.toLowerCase();
        return containsEither(lc, "netherlands", "nederland", "dutch") &&
            !containsEither(lc, "antillen", "antilles");
    }

    /**
     * Check if it contains a french title (French, France, Belgium /w French comment)
     *
     * @return is french
     */
    public boolean isFrench() {
        if (country == null) {
            return false;
        }
        String lc = country.toLowerCase();
        String lcComment = comment != null ? comment.toLowerCase() : "";
        return containsEither(lc, "french", "france") ||
            (lc.contains("belgium") && lcComment.contains("french"));
    }

    private boolean containsEither(String s, String... options) {
        for (String option : options) {
            if (s.contains(option)) {
                return true;
            }
        }
        return false;
    }


}
