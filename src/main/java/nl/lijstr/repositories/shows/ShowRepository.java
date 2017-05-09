package nl.lijstr.repositories.shows;

import java.util.List;
import java.util.Optional;
import nl.lijstr.domain.shows.Show;
import nl.lijstr.repositories.abs.BasicRepository;

/**
 * The basic Show repository.
 */
public interface ShowRepository extends BasicRepository<Show> {

    /**
     * Get all shows ordered by title.
     *
     * @return list of shows
     */
    List<Show> findAllByOrderByTitleAsc();

    /**
     * Find the show with the oldest lastUpdated value.
     *
     * @return the show
     */
    Show findFirstByOrderByLastUpdatedAsc();

    /**
     * Find a show by it's IMDB ID.
     *
     * @param imdbId The IMDB ID
     *
     * @return the show
     */
    Optional<Show> findByImdbId(String imdbId);

}
