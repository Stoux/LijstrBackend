package nl.lijstr.repositories.abs;

import java.util.List;
import nl.lijstr.domain.shows.Show;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Create a BasicShowRepository.
 * This repository contains items that are linked to a show.
 *
 * @param <T> The model
 */
@NoRepositoryBean
public interface BasicShowRepository<T> extends BasicRepository<T> {

    /**
     * Find all items linked to a Show.
     *
     * @param show The show
     *
     * @return the items
     */
    List<T> findByShow(Show show);

}
