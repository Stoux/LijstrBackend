package nl.lijstr.repositories.abs;

import java.util.List;
import nl.lijstr.domain.interfaces.Target;
import nl.lijstr.domain.movies.Movie;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Created by Stoux on 09/05/2017.
 */
@NoRepositoryBean
public interface BasicTargetRepository<T extends Target> extends BasicRepository<T> {

    /**
     * Get all items ordered by title.
     *
     * @return list of items
     */
    List<T> findAllByOrderByTitleAsc();

    /**
     * Find the item with the oldest lastUpdated value.
     *
     * @return the item
     */
    T findFirstByOrderByLastUpdatedAsc();

    /**
     * Find an item by it's IMDB ID.
     *
     * @param imdbId The IMDB ID
     *
     * @return the item or null
     */
    T findByImdbId(String imdbId);

}
