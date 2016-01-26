package nl.lijstr.exceptions.db;

import nl.lijstr.exceptions.LijstrException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * A LijstrException that can be thrown when a certain item is not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends LijstrException {

    /**
     * A NotFoundException.
     *
     * @param name The name of the item
     * @param id   The id of the item
     */
    public NotFoundException(String name, long id) {
        super("No item (" + name + ") with ID " + id + " found.");
    }

}
