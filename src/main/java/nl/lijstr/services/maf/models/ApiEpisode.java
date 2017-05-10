package nl.lijstr.services.maf.models;

import com.google.gson.annotations.SerializedName;
import lombok.*;

/**
 * Created by Stoux on 03/12/2015.
 */
@NoArgsConstructor
@Getter
@ToString
public class ApiEpisode {

    @SerializedName("episode")
    private Integer episodeNumber;
    private String title;
    private String date;
    private String plot;
    @SerializedName("idIMDB")
    private String imdbId;
    @SerializedName("urlPoster")
    private String posterUrl;

}
