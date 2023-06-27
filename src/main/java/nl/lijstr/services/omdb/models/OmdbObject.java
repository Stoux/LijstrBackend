package nl.lijstr.services.omdb.models;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OmdbObject {

    @SerializedName("imdbID")
    private String imdbId;

    @SerializedName("Title")
    private String title;

    @SerializedName("Year")
    private String year;

    @SerializedName("Released")
    private String released;
    @SerializedName("Runtime")
    private String runtime;

    private String imdbRating;
    private String imdbVotes;

    @SerializedName("Type")
    private String type;

    @SerializedName("Plot")
    private String plot;

    @SerializedName("Poster")
    private String poster;

    @SerializedName("Response")
    private String response;

    @SerializedName("Error")
    private String error;

    public OmdbObject(String title, String year, String imdbRating, String type) {
        this.title = title;
        this.year = year;
        this.imdbRating = imdbRating;
        this.type = type;
    }

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

    /**
     * Check if the response object was a success.
     *
     * @return was successful
     */
    public boolean isSuccessful() {
        return "True".equalsIgnoreCase(response);
    }

}
