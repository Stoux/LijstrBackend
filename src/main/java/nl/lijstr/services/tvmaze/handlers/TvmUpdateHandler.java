package nl.lijstr.services.tvmaze.handlers;

import io.jsonwebtoken.lang.Objects;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import nl.lijstr.common.CreatingContainer;
import nl.lijstr.domain.other.DateAccuracy;
import nl.lijstr.domain.shows.Show;
import nl.lijstr.domain.shows.episodes.ShowEpisode;
import nl.lijstr.domain.shows.seasons.ShowSeason;
import nl.lijstr.exceptions.LijstrException;
import nl.lijstr.processors.annotations.InjectLogger;
import nl.lijstr.repositories.other.FieldHistoryRepository;
import nl.lijstr.repositories.other.FieldHistorySuggestionRepository;
import nl.lijstr.repositories.shows.ShowRepository;
import nl.lijstr.services.common.ShowSeasonUpdater;
import nl.lijstr.services.maf.handlers.util.FieldModifyHandler;
import nl.lijstr.services.tvmaze.models.TvmEpisode;
import nl.lijstr.services.tvmaze.models.TvmSeason;
import nl.lijstr.services.tvmaze.models.TvmShow;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * TvMaze component for updating {@link Show}s.
 */
@Component
public class TvmUpdateHandler {

    @InjectLogger
    private Logger logger;

    private final FieldHistoryRepository historyRepository;
    private final FieldHistorySuggestionRepository suggestionRepository;
    private final ShowRepository showRepository;

    @Autowired
    public TvmUpdateHandler(FieldHistoryRepository historyRepository,
                            FieldHistorySuggestionRepository suggestionRepository, ShowRepository showRepository) {
        this.historyRepository = historyRepository;
        this.suggestionRepository = suggestionRepository;
        this.showRepository = showRepository;
    }

    /**
     * Update a Show using TvMaze data.
     *
     * @param show    The domain model
     * @param apiShow The api model
     *
     * @return the updated show
     */
    public Show update(Show show, TvmShow apiShow) {
        if (!Objects.nullSafeEquals(show.getTvMazeId(), apiShow.getId())) {
            throw new LijstrException("TvMaze IDs are not equal");
        }

        FieldModifyHandler handler = new FieldModifyHandler(show, apiShow, historyRepository, suggestionRepository);
        handler.modify("title");
        handler.modify("scriptType");
        handler.modify("status");


        Show updatedShow = createUpdater().updateSeasons(show, apiShow.getSeasons());
        return showRepository.saveAndFlush(updatedShow);
    }

    private void updateSeason(ShowSeason showSeason, TvmSeason apiSeason) {
        if (showSeason.getTvMazeId() == null) {
            showSeason.setTvMazeId(apiSeason.getId());
        }

        if (!StringUtils.isEmpty(apiSeason.getTitle())) {
            new FieldModifyHandler(showSeason, apiSeason, historyRepository, suggestionRepository).modify("title");
        }
    }

    private void updateEpisode(ShowEpisode showEpisode, TvmEpisode apiEpisode) {
        CreatingContainer<FieldModifyHandler> handler = new CreatingContainer<>(
            () -> new FieldModifyHandler(showEpisode, apiEpisode, historyRepository, suggestionRepository));

        if (showEpisode.getTvMazeId() == null) {
            showEpisode.setTvMazeId(apiEpisode.getId());
        }

        if (apiEpisode.getTitle() != null) {
            handler.getItem().modify("title");
        }

        //Don't update airDates with TIME accuracy that are in the past (not worth / can cause conflicts when schedule changes)
        //Always override anything lower than TIME (TvMaze > MAF)
        LocalDateTime apiAirstamp = apiEpisode.getAirTimestamp();
        if (apiAirstamp != null && (showEpisode.getAirDateAccuracy() != DateAccuracy.TIME || apiAirstamp
            .isAfter(LocalDateTime.now()))) {
            if (showEpisode.getAirDateAccuracy() != DateAccuracy.TIME) {
                showEpisode.setAirDate(apiAirstamp);
                showEpisode.setAirDateAccuracy(DateAccuracy.TIME);
            } else {
                handler.getItem()
                    .compareAndModify("airDate", showEpisode.getAirDate(), apiAirstamp, s -> s,
                        showEpisode::setAirDate);
            }
        }

        if (apiEpisode.getRuntime() != null) {
            handler.getItem().modify("runtime");
        }
    }

    private ShowSeasonUpdater<TvmEpisode, TvmSeason> createUpdater() {
        return new ShowSeasonUpdater<>(logger, showRepository, this::updateSeason, this::updateEpisode,
            (show, apiSeason) -> new ShowSeason(show, apiSeason.getId(), apiSeason.getSeasonNumber()),
            (season, apiEpisode) -> new ShowEpisode(apiEpisode.getId(), season, apiEpisode.getEpisodeNumber()));
    }

}
