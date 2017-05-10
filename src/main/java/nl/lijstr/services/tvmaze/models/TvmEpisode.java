package nl.lijstr.services.tvmaze.models;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.*;
import nl.lijstr.services.common.ShowSeasonUpdater;

/**
 * TvMaze.com Show's episode model.
 */
@Getter
public class TvmEpisode implements ShowSeasonUpdater.Episode {

    private long id;
    @SerializedName("name")
    private String title;

    private Integer season;
    @SerializedName("number")
    private Integer episodeNumber;

    @Getter(AccessLevel.NONE)
    @SerializedName("airstamp")
    private String airTimestamp;

    private Integer runtime;

    @SerializedName("image")
    private TvmImageHolder images;

    private String summary;

    public LocalDateTime getAirTimestamp() {
        return airTimestamp == null ? null : ZonedDateTime.parse(airTimestamp).withZoneSameInstant(
            ZoneId.systemDefault()).toLocalDateTime();
    }
}
