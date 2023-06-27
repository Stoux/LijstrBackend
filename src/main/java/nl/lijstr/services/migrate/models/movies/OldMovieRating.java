package nl.lijstr.services.migrate.models.movies;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A data object that represents a rating as given on the old site.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OldMovieRating {

    @SerializedName("Gezien")
    private String seen;
    @SerializedName("Rating")
    private String rating;
    @SerializedName("Comment")
    private String comment;

}
