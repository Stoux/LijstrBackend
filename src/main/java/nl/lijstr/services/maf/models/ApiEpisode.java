package nl.lijstr.services.maf.models;

import com.google.gson.annotations.SerializedName;
import lombok.*;
import nl.lijstr.services.common.ShowSeasonUpdater;

/**
 * Created by Stoux on 03/12/2015.
 */
@NoArgsConstructor
@Getter
@ToString
public class ApiEpisode implements ShowSeasonUpdater.Episode {

    @SerializedName("episode")
    private Integer episodeNumber;
    private String title;
    private String date;
    private String plot;
    @SerializedName("idIMDB")
    private String imdbId;
    @SerializedName("urlPoster")
    private String posterUrl;

    /**
     * Check if this episode has a valid title.
     *
     * @return is valid
     */
    public boolean hasValidTitle() {
        return title != null && !title.matches("Episode #\\d+\\.\\d+");
    }

    /**
     * Check if this episode has a valid plot.
     *
     * @return is valid
     */
    public boolean hasValidPlot() {
        return plot != null && !("Know what this is about? Be the first one to add a plot.".equalsIgnoreCase(
            plot) || "The plot is unknown at this time".equalsIgnoreCase(plot));
    }

}
