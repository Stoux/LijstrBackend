package nl.lijstr.repositories.shows;

import nl.lijstr.domain.shows.Show;
import nl.lijstr.repositories.abs.BasicRepository;

/**
 * The basic TV Show repository.
 */
public interface ShowRepository extends BasicRepository<Show> {

    /**
     * Fetch a show by it's TMDB ID.
     * @param tmdbId The TMDB ID
     * @return The show (if any)
     */
    Show getByTmdbId( int tmdbId );

}
