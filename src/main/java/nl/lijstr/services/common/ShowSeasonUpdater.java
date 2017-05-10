package nl.lijstr.services.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import nl.lijstr.domain.shows.Show;
import nl.lijstr.domain.shows.episodes.ShowEpisode;
import nl.lijstr.domain.shows.seasons.ShowSeason;
import nl.lijstr.repositories.abs.BasicRepository;
import nl.lijstr.repositories.shows.ShowRepository;
import org.apache.logging.log4j.Logger;

/**
 * Created by Stoux on 10/05/2017.
 */
public class ShowSeasonUpdater<ApiEpisode extends ShowSeasonUpdater.Episode, ApiSeason extends ShowSeasonUpdater.Season<ApiEpisode>> {

    private Logger logger;

    private BasicRepository<Show> showRepository;

    private BiConsumer<ShowSeason, ApiSeason> seasonUpdater;
    private BiConsumer<ShowEpisode, ApiEpisode> episodeUpdater;

    private ShowSeasonCreator<ApiSeason> seasonCreator;
    private ShowEpisodeCreator<ApiEpisode> episodeCreator;

    public ShowSeasonUpdater(Logger logger, BasicRepository<Show> showRepository,
                             BiConsumer<ShowSeason, ApiSeason> seasonUpdater,
                             BiConsumer<ShowEpisode, ApiEpisode> episodeUpdater,
                             ShowSeasonCreator<ApiSeason> seasonCreator,
                             ShowEpisodeCreator<ApiEpisode> episodeCreator) {
        this.logger = logger;
        this.showRepository = showRepository;
        this.seasonUpdater = seasonUpdater;
        this.episodeUpdater = episodeUpdater;
        this.seasonCreator = seasonCreator;
        this.episodeCreator = episodeCreator;
    }

    public Show updateSeasons(final Show show, final List<ApiSeason> apiSeasons) {
        List<ShowSeason> seasons = show.getSeasons();

        ArrayList<ApiSeason> newSeasons = new ArrayList<>();
        for (ApiSeason apiSeason : apiSeasons) {
            //Find the matching ShowSeason
            Optional<ShowSeason> foundSeason = getShowSeason(seasons, apiSeason);

            if (foundSeason.isPresent()) {
                updateSeason(foundSeason.get(), apiSeason);
            } else {
                ShowSeason newSeason = seasonCreator.create(show, apiSeason);
                seasons.add(newSeason);
                newSeasons.add(apiSeason);
            }
        }

        if (newSeasons.isEmpty()) {
            return show;
        }

        //New seasons are added, they need to be persisted before we can add episodes to them
        Show updatedShow = showRepository.saveAndFlush(show);

        for (ApiSeason apiSeason : apiSeasons) {
            Optional<ShowSeason> foundSeason = getShowSeason(updatedShow.getSeasons(), apiSeason);
            if (!foundSeason.isPresent()) {
                logger.warn("Unable to find season {} after adding it...", apiSeason.getSeasonNumber());
            } else {
                updateSeason(foundSeason.get(), apiSeason);
            }
        }

        return updatedShow;
    }

    private Optional<ShowSeason> getShowSeason(List<ShowSeason> seasons, ApiSeason apiSeason) {
        return seasons.stream()
            .filter(s -> Objects.equals(s.getSeasonNumber(), apiSeason.getSeasonNumber()))
            .findFirst();
    }

    private void updateSeason(ShowSeason showSeason, ApiSeason apiSeason) {
        //Update any values (if any)
        if (seasonUpdater != null) {
            seasonUpdater.accept(showSeason, apiSeason);
        }

        //Update it's episodes
        for (ApiEpisode apiEpisode : apiSeason.getEpisodes()) {
            //Find the matching ShowEpisode
            List<ShowEpisode> showEpisodes = showSeason.getEpisodes();
            Optional<ShowEpisode> foundEpisode = showEpisodes.stream()
                .filter(e -> Objects.equals(e.getEpisodeNumber(), apiEpisode.getEpisodeNumber()))
                .findFirst();

            ShowEpisode showEpisode = foundEpisode.orElseGet(() -> {
                ShowEpisode newEpisode = episodeCreator.create(showSeason, apiEpisode);
                showEpisodes.add(newEpisode);
                return newEpisode;
            });

            //Update the episode
            episodeUpdater.accept(showEpisode, apiEpisode);
        }
    }

    public interface Season<X extends Episode> {
        Integer getSeasonNumber();

        List<X> getEpisodes();
    }

    public interface Episode {
        Integer getEpisodeNumber();
    }

    @FunctionalInterface
    public interface ShowSeasonCreator<X> {
        ShowSeason create(Show show, X apiSeason);
    }

    @FunctionalInterface
    public interface ShowEpisodeCreator<X> {
        ShowEpisode create(ShowSeason season, X apiEpisode);
    }

}
