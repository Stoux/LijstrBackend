package nl.lijstr.repositories.shows;

import nl.lijstr.domain.shows.Show;
import nl.lijstr.repositories.abs.BasicRepository;

import java.util.List;

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

    /**
     * Get all movies ordered by title.
     *
     * @return list of movies
     */
    List<Show> findAllByOrderByTitleAsc();

}
