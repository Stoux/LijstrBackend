package nl.lijstr.services.maf.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.*;
import nl.lijstr.services.common.ShowSeasonUpdater;

/**
 * Created by Stoux on 03/12/2015.
 */
@NoArgsConstructor
@Getter
@ToString
public class ApiSeason implements ShowSeasonUpdater.Season<ApiEpisode> {

    @SerializedName("numSeason")
    private Integer seasonNumber;
    private List<ApiEpisode> episodes;

}
