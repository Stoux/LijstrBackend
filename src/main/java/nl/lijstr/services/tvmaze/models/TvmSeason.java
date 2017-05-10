package nl.lijstr.services.tvmaze.models;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.*;
import nl.lijstr.services.common.ShowSeasonUpdater;

/**
 * TvMaze.com Show's season model.
 */
@Getter
public class TvmSeason implements ShowSeasonUpdater.Season<TvmEpisode> {

    private long id;
    @SerializedName("number")
    private Integer seasonNumber;
    @SerializedName("name")
    private String title;

    @SerializedName("episodeOrder")
    private Integer numberOfEpisodes;
    
    private String summary;

    @Getter(AccessLevel.NONE)
    private List<TvmEpisode> episodes;

    /**
     * Add an episode to this season.
     *
     * @param episode The episode
     */
    public void addEpisode(TvmEpisode episode) {
        initEpisodes();

        if (!Objects.equals(episode.getSeason(), getSeasonNumber())) {
            throw new IllegalArgumentException("Adding incorrect episode (wrong season)");
        }

        this.episodes.add(episode);
    }

    @Override
    public List<TvmEpisode> getEpisodes() {
        initEpisodes();
        return episodes;
    }

    private void initEpisodes() {
        if (this.episodes == null) {
            this.episodes = new ArrayList<>();
        }
    }

}
