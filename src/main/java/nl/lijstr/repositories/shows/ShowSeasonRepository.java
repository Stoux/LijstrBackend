package nl.lijstr.repositories.shows;

import nl.lijstr.domain.shows.Show;
import nl.lijstr.domain.shows.ShowSeason;
import nl.lijstr.repositories.abs.BasicRepository;

/**
 * The basic TV Show Seasons repository.
 */
public interface ShowSeasonRepository extends BasicRepository<ShowSeason> {


    /**
     * Fetch a season by it's show and season number.
     *
     * @param showId Show ID
     * @param seasonNumber Chronological season number
     *
     * @return the season
     */
    ShowSeason getByShowIdAndSeasonNumber(Long showId, Integer seasonNumber);

}
