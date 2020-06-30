package nl.lijstr.beans.shows;

import nl.lijstr.api.shows.models.ShowSummary;
import nl.lijstr.domain.shows.Show;
import nl.lijstr.domain.shows.ShowEpisode;
import nl.lijstr.domain.shows.ShowSeason;
import nl.lijstr.repositories.shows.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class ShowSummaryBean {

    @Autowired
    private ShowRepository showRepository;

    /**
     * Fetch all shows and build summaries of them.
     * @return the summaries
     */
    public List<ShowSummary> buildSummaries() {
        final List<Show> shows = showRepository.findAllByOrderByTitleAsc();
        final List<ShowSummary> result = new ArrayList<>(shows.size());

        for (final Show show : shows) {
            // TODO: Optimize
            result.add(convert(show));
        }

        return result;
    }

    private ShowSummary convert(final Show show) {
        final ShowSummary.ShowSummaryBuilder builder = ShowSummary.builder()
            .id(show.getId())
            .title(show.getTitle())
            .imdbRating(show.getImdbRating())
            .tmdbRating(show.getTmdbRating())
            .status(show.getStatus())
            .type(show.getType());

        // Determine the seasons & episodes
        // TODO: This is next-gen inefficient
        int seasonCount = 0;
        int episodeCount = 0;

        final List<ShowSeason> seasons = show.getSeasons();
        for (final ShowSeason season : seasons) {

            // Check if there are any episodes
            final List<ShowEpisode> episodes = season.getEpisodes();
            if (!episodes.isEmpty()) {

                // First episode of the first season
                if (season.getSeasonNumber() == 1) {
                    final LocalDate firstAirDate = episodes.get(0).getAirDate();
                    if (firstAirDate != null) {
                        builder.startYear(firstAirDate.getYear());
                    }
                }

                // Last / Most recent season
                if (season.getSeasonNumber() == seasons.size()) {
                    if (show.getInProduction()) {
                        // Find the first episode that's going to air
                        final LocalDate now = LocalDate.now();
                        for (final ShowEpisode episode : season.getEpisodes()) {
                            final LocalDate airDate = episode.getAirDate();
                            if (airDate.isEqual(now) || airDate.isAfter(now)) {
                                // We count today.
                                builder.nextEpisode(airDate);
                                break;
                            }
                        }

                    } else {
                        final LocalDate lastAirDate = episodes.get(episodes.size() - 1).getAirDate();
                        if (lastAirDate != null) {
                            builder.endYear(lastAirDate.getYear());
                        }
                    }
                }
            }


            seasonCount++;
            episodeCount += episodes.size();
        }

        builder.episodes(episodeCount).seasons(seasonCount);

        // TODO: Ratings


        return builder.build();
    }



}
