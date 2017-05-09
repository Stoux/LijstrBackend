package nl.lijstr.api.shows.models;

import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import nl.lijstr.domain.shows.episodes.ShowEpisode;
import nl.lijstr.domain.shows.seasons.ShowSeason;

/**
 * A view model based on a {@link ShowSeason},
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShowSeasonDetail {

    private Integer seasonNumber;
    private String plot;
    private Integer episodes;
    private ShowStatus status;

    /**
     * Create a {@link ShowSeasonDetail} from a {@link ShowSeason}.
     *
     * @param season The season
     *
     * @return the detail
     */
    public static ShowSeasonDetail fromSeason(ShowSeason season) {
        List<ShowEpisode> episodeList = season.getEpisodes();

        //Determine the status
        ShowStatus status;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstEpisode = episodeList.get(0).getAirDate();
        LocalDateTime lastEpisode = episodeList.get(episodeList.size() - 1).getAirDate();
        if (firstEpisode.isAfter(now)) {
            status = ShowStatus.PLANNED;
        } else if (lastEpisode.isBefore(now)) {
            status = ShowStatus.AIRED;
        } else {
            status = ShowStatus.RUNNING;
        }

        return new ShowSeasonDetail(season.getSeasonNumber(), season.getPlot(), episodeList.size(), status);
    }

}
