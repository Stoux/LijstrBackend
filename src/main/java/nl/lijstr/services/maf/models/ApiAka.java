package nl.lijstr.services.maf.models;

import lombok.*;

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
        if (country == null) return false;
        String lc = country.toLowerCase();
        return (lc.contains("netherlands") || lc.contains("nederland") || lc.contains("dutch")) &&
                !(lc.contains("antillen") || lc.contains("antilles"));
    }

    /**
     * Check if it contains a french title (French, France, Belgium /w French comment)
     *
     * @return is french
     */
    public boolean isFrench() {
        if (country == null) return false;
        String lc = country.toLowerCase();
        String lcComment = comment != null ? comment.toLowerCase() : "";
        return lc.contains("french") || lc.contains("france") ||
                (lc.contains("belgium") && lcComment.contains("french"));
    }

}
