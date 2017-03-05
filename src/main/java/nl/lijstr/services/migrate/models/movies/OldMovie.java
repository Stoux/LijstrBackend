package nl.lijstr.services.migrate.models.movies;

import com.google.gson.annotations.SerializedName;
import java.util.Map;
import lombok.*;

/**
 * Created by Stoux on 31-1-2017.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OldMovie {

    @SerializedName("FilmNr")
    private Long id;
    @SerializedName("FilmTitel")
    private String title;
    @SerializedName("FilmJaar")
    private Integer year;
    @SerializedName("FilmIMDBLink")
    private String imdbLink;
    @SerializedName("TrailerLink")
    private String youtubeId;

    @SerializedName("Ratings")
    private Map<String, OldMovieRating> ratings;

    public OldMovie(Long id, String title, String imdbLink) {
        this.id = id;
        this.title = title;
        this.imdbLink = imdbLink;
    }

}
