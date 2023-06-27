package nl.lijstr.repositories.abs;

import nl.lijstr.domain.other.ApprovedFor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * A repository base for items that have a {@link ApprovedFor}.
 * Adds a method that selects a random item while taking approvedFor in consideration.
 *
 * @param <T> The model
 */
@NoRepositoryBean
public interface RandomApprovalRepository<T> extends BasicRepository<T> {

    /**
     * Find a random approved item.
     *
     * @param approvedFor max approvedFor
     *
     * @return the item or null
     */
    @Query("select x from #{#entityName} x where x.approvedFor <= ?1 order by rand()")
    List<T> findRandomForApproved(ApprovedFor approvedFor);

}
