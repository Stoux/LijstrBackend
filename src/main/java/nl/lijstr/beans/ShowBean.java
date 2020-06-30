package nl.lijstr.beans;

import com.uwetrottmann.tmdb2.entities.Translations;
import com.uwetrottmann.tmdb2.entities.TvEpisode;
import com.uwetrottmann.tmdb2.entities.TvSeason;
import com.uwetrottmann.tmdb2.entities.TvShow;
import io.sentry.Sentry;
import nl.lijstr.common.Utils;
import nl.lijstr.domain.shows.Show;
import nl.lijstr.domain.shows.ShowEpisode;
import nl.lijstr.domain.shows.ShowSeason;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.BadRequestException;
import nl.lijstr.repositories.other.FieldHistoryRepository;
import nl.lijstr.repositories.other.FieldHistorySuggestionRepository;
import nl.lijstr.repositories.shows.ShowEpisodeRepository;
import nl.lijstr.repositories.shows.ShowRepository;
import nl.lijstr.repositories.shows.ShowSeasonRepository;
import nl.lijstr.services.maf.handlers.util.FieldModifyHandler;
import nl.lijstr.services.tmdb.TmdbApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Bean with utility methods regarding TV shows.
 */
@Component
public class ShowBean {

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ShowSeasonRepository showSeasonRepository;

    @Autowired
    private ShowEpisodeRepository showEpisodeRepository;

    @Autowired
    private TmdbApiService tmdbApiService;

    @Autowired
    private FieldHistoryRepository historyRepository;
    @Autowired
    private FieldHistorySuggestionRepository suggestionRepository;

    /**
     * Add a new show by it's IMDB ID.
     *
     * @param imdbId  The IMDB ID
     * @param addedBy Added by this user (if any)
     * @return The new show
     */
    public Show addShowByImdbId(final String imdbId, final User addedBy) {
        final Optional<Integer> optFoundId = tmdbApiService.findShowByImdbId(imdbId);
        if (!optFoundId.isPresent()) {
            throw new BadRequestException("No TMDB show found with that IMDB ID");
        }
        return addShowByTmdbId(optFoundId.get(), addedBy);
    }

    /**
     * Add a new show by it's TMDB ID.
     *
     * @param tmdbId  The TMDB ID
     * @param addedBy Added by this user (if any)
     * @return The new show
     */
    public Show addShowByTmdbId(final int tmdbId, final User addedBy) {
        // Check if the show doesn't exist yet.
        if (showRepository.getByTmdbId(tmdbId) != null) {
            throw new BadRequestException("Show with TMDB ID " + tmdbId + " already exists.");
        }

        // Create the new show.
        final Show newShow = new Show(tmdbId, addedBy);
        final Show savedShow = showRepository.save(newShow);
        return updateShow(savedShow);
    }

    /**
     * Update a {@link Show} including it's {@link ShowSeason}s and {@link ShowEpisode}s.
     *
     * @param show The show to update
     * @return The updated show
     */
    public Show updateShow(final Show show) {
        // Fetch the latest from TMDB
        final TvShow tmdbShow = tmdbApiService.getShow(show.getTmdbId());

        // Process main info
        final FieldModifyHandler handler = new FieldModifyHandler(show, tmdbShow, true, historyRepository, suggestionRepository);
        handler.modify("title", "name");
        handler.modify("originalTitle", "original_name");
        handler.modify("overview");

        handler.modify("tmdbRating", "vote_average");
        handler.modify("tmdbVotes", "vote_count");
        handler.modify("tmdbPopularity", "popularity");

        handler.modify("status");
        handler.modify("type");
        handler.modify("inProduction", "in_production");

        handler.modify("backdropImage", "backdrop_path");
        handler.modify("posterImage", "poster_path");

        // TODO: Episode run time
        // TODO: Genres?
        // TODO: Languages?
        // TODO: Networks?
        // TODO: Created by?

        // Attempt to find the dutch title
        if (tmdbShow.translations != null && tmdbShow.translations.translations != null) {
            for (Translations.Translation translation : tmdbShow.translations.translations) {
                if ("NL".equals(translation.iso_639_1) && translation.data != null && !StringUtils.isEmpty(translation.data.title)) {
                    handler.modifyWithValue("dutchTitle", translation.data.title);
                    break;
                }
            }
        }

        // External IDs
        if (tmdbShow.external_ids != null) {
            handler.modifyWithValue("imdbId", tmdbShow.external_ids.imdb_id);
            handler.modifyWithValue("tvdbId", tmdbShow.external_ids.tvdb_id);
        }

        if (show.getImdbId() != null) {
            // TODO: Get IMDB rating & votes from OmdbApi
        }

        // === Seasons ===
        updateSeasons(show, tmdbShow);

        show.setLastUpdated(LocalDateTime.now());

        return showRepository.saveAndFlush(show);
    }

    private void updateSeasons(final Show show, final TvShow tmdbShow) {
        if (tmdbShow.seasons == null) {
            return;
        }

        // Map the season to their TMDB IDs.
        final List<ShowSeason> currentSeasons = show.getSeasons();
        final Map<Integer, ShowSeason> tmdbToCurrentSeason = Utils.toMap(currentSeasons, ShowSeason::getTmdbId);

        // Loop through the new seasons
        for (final TvSeason tmdbSeason : tmdbShow.seasons) {
            ShowSeason localSeason = tmdbToCurrentSeason.get(tmdbSeason.id);
            if (localSeason == null) {
                // Season doesn't exist yet locally, create it.
                localSeason = new ShowSeason(
                    show, tmdbSeason.id, tmdbSeason.season_number, tmdbSeason.name, tmdbSeason.overview, tmdbSeason.poster_path
                );
                localSeason = this.showSeasonRepository.save(localSeason);
                currentSeasons.add(localSeason);
            } else {
                // Already exists, update the fields if necessary
                final FieldModifyHandler handler = new FieldModifyHandler(
                    localSeason, tmdbSeason, true, historyRepository, suggestionRepository
                );
                handler.modify("title", "name");
                handler.modify("overview");
                handler.modify("posterImage", "poster_path");

                if (!Objects.equals(tmdbSeason.season_number, localSeason.getSeasonNumber())) {
                    Sentry.capture(String.format(
                        "Season number mismatch. Show %d | LS %d | TS %S", show.getId(), localSeason.getId(), tmdbSeason.id
                    ));
                    // TODO: Major issue. Report error.
                }

                if (handler.isUpdated()) {
                    localSeason = showSeasonRepository.saveAndFlush(localSeason);
                }
            }

            // Update it's episodes
            updateSeasonEpisodes(show, localSeason);
        }
    }

    private void updateSeasonEpisodes(final Show show, final ShowSeason season) {
        final TvSeason tmdbSeason = tmdbApiService.getSeason(show.getTmdbId(), season.getSeasonNumber());
        if (tmdbSeason.episodes == null) {
            // No episode
            return;
        }

        // Map the episodes to their TMDB IDs.
        final List<ShowEpisode> currentEpisodes = season.getEpisodes();
        final Map<Integer, ShowEpisode> tmdbToCurrentEpisode = Utils.toMap(currentEpisodes, ShowEpisode::getTmdbId);

        // Loop through the episodes
        for (TvEpisode tmdbEpisode : tmdbSeason.episodes) {
            ShowEpisode localEpisode = tmdbToCurrentEpisode.get(tmdbEpisode.id);
            boolean newEpisode = false;
            if (localEpisode == null) {
                // Episode doesn't exist yet. Create it.
                localEpisode = new ShowEpisode(show, season, tmdbEpisode.id);
                newEpisode = true;
            }

            // Update it fields.
            final FieldModifyHandler handler = new FieldModifyHandler(localEpisode, tmdbEpisode, true, historyRepository, suggestionRepository);
            handler.modify("episodeNumber", "episode_number");
            handler.compareAndModify(
                "airDate", localEpisode.getAirDate(), tmdbEpisode.air_date,
                date -> date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), localEpisode::setAirDate
            );

            handler.modify("title", "name");
            handler.modify("overview");
            handler.modify("productionCode", "production_code");
            handler.modify("screenshotImage", "still_path");
            handler.modify("tmdbRating", "vote_average");
            handler.modify("tmdbVotes", "vote_count");

            // Update if needed
            if (handler.isUpdated() || newEpisode) {
                localEpisode = showEpisodeRepository.saveAndFlush(localEpisode);
            }

            // Add to the list of episodes of this show
            if (newEpisode) {
                currentEpisodes.add(localEpisode);
            }
        }
    }


}
