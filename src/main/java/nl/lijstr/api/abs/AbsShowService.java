package nl.lijstr.api.abs;

import nl.lijstr.domain.shows.Show;
import nl.lijstr.repositories.shows.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for services / endpoints that deal with {@link Show}s.
 */
public abstract class AbsShowService extends AbsService {

    @Autowired
    protected ShowRepository showRepository;

    /**
     * Find a {@link Show}.
     *
     * @param id The ID of the Show
     *
     * @return the show
     */
    protected Show findShow(Long id) {
        return findOne(showRepository, id, "Show");
    }

}
