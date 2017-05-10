package nl.lijstr.services.maf.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.*;

/**
 * Created by Stoux on 03/12/2015.
 */
@NoArgsConstructor
@Getter
@ToString
public class ApiSeason {

    @SerializedName("numSeason")
    private Integer seasonNumber;
    private List<ApiEpisode> episodes;

}
