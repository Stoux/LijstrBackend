package nl.lijstr.exceptions.db;

import nl.lijstr.exceptions.LijstrException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception used for when a conflict occurs somewhere.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends LijstrException {

    /**
     * Create a {@link ConflictException}.
     *
     * @param message The message
     */
    public ConflictException(String message) {
        super(message);
    }
}
