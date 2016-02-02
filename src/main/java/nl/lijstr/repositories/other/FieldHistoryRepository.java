package nl.lijstr.repositories.other;

import java.util.List;
import nl.lijstr.domain.other.FieldHistory;
import nl.lijstr.repositories.abs.BasicRepository;

/**
 * The basic FieldHistory repository.
 */
public interface FieldHistoryRepository extends BasicRepository<FieldHistory> {

    /**
     * Find all FieldHistory entries by className.
     *
     * @param objectId  The ID of the object
     * @param className The class name
     *
     * @return the list of entries
     */
    List<FieldHistory> findByObjectIdAndClassName(long objectId, String className);


}
