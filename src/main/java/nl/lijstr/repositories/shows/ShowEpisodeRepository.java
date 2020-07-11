package nl.lijstr.repositories.shows;

import nl.lijstr.domain.shows.Show;
import nl.lijstr.domain.shows.ShowEpisode;
import nl.lijstr.repositories.abs.BasicRepository;

import java.util.Collection;
import java.util.List;

/**
 * The basic TV Show Episodes repository.
 */
public interface ShowEpisodeRepository extends BasicRepository<ShowEpisode> {


    List<ShowEpisode> findAllByIdIn(Collection<Long> id);

    ShowEpisode getByShowIdAndSeasonSeasonNumberAndEpisodeNumber(Long showId, Integer seasonNumber, Integer episodeNumber);


}
