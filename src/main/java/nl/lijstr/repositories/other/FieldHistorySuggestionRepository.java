package nl.lijstr.repositories.other;

import nl.lijstr.domain.other.FieldHistory;
import nl.lijstr.domain.other.FieldHistorySuggestion;
import nl.lijstr.repositories.abs.BasicRepository;

/**
 * The FieldHistorySuggestion repository.
 */
public interface FieldHistorySuggestionRepository extends BasicRepository<FieldHistorySuggestion> {

    /**
     * Find a Suggestion by it's FieldHistory object.
     *
     * @param fieldHistory The history object
     *
     * @return the suggestion
     */
    FieldHistorySuggestion findByFieldHistory(FieldHistory fieldHistory);

}
