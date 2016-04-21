package nl.lijstr.services.omdb.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Leon Stam on 21-4-2016.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OmdbObject {

    @SerializedName("Title")
    private String title;
    @SerializedName("Year")
    private String year;
    private String imdbRating;
    @SerializedName("Type")
    private String type;

    /**
     * Check if this a movie.
     *
     * @return is a movie
     */
    public boolean isMovie() {
        return "movie".equals(type);
    }

    /**
     * Check if this is a series.
     *
     * @return is a series
     */
    public boolean isSeries() {
        return "series".equals(type);
    }

}
