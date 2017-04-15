package nl.lijstr.repositories.abs;

import java.util.List;
import nl.lijstr.domain.base.IdModel;
import nl.lijstr.domain.base.RatingModel;
import nl.lijstr.domain.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * A basic rating repository which allows for common actions for {@link RatingModel}s.
 *
 * @param <Target> The target of the {@link RatingModel}
 * @param <T>      The extended class of the {@link RatingModel}
 */
@NoRepositoryBean
public interface BasicRatingRepository<Target extends IdModel, T extends RatingModel<Target>> extends BasicRepository<T> {

    /**
     * Find all items linked to the target.
     *
     * @param target The target
     *
     * @return the items
     */
    List<T> findByTarget(Target target);

    /**
     * Find the latest rating for a certain target and user.
     *
     * @param target The target
     * @param user   The user
     *
     * @return the item
     */
    T findByTargetAndUserAndLatestIsTrue(Target target, User user);

    /**
     * Find all (paged) ratings with a given Seen value.
     *
     * @param pageable The requested page
     * @param seen     The seen value
     *
     * @return a paged result
     */
    Page<T> findAllBySeenEquals(Pageable pageable, RatingModel.Seen seen);

}
