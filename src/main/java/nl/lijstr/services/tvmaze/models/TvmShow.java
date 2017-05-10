package nl.lijstr.services.tvmaze.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Objects;
import lombok.*;

/**
 * TvMaze.com Show model.
 */
@Getter
public class TvmShow {

    private long id;
    @SerializedName("name")
    private String title;
    @SerializedName("type")
    private String scriptType;
    private String status;

    private String[] genres;
    @SerializedName("image")
    private TvmImageHolder images;

    @Getter(AccessLevel.NONE)
    @SerializedName("_embedded")
    private TvmEmbedded embedded;

    public class TvmEmbedded {

        private boolean migrated = false;
        private List<TvmEpisode> episodes;
        private List<TvmSeason> seasons;

    }

    public List<TvmSeason> getSeasons() {
        if (!embedded.migrated) {
            //Store the episodes in the seasons to match MAF Structure
            embedded.migrated = true;

            List<TvmSeason> seasons = embedded.seasons;
            for (TvmEpisode episode : this.embedded.episodes) {
                for (TvmSeason season : seasons) {
                    if (Objects.equals(season.getSeasonNumber(), episode.getSeason())) {
                        season.addEpisode(episode);
                    }
                }
            }
        }

        return embedded.seasons;
    }


}



