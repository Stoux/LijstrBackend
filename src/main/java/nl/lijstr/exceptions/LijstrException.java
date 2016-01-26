package nl.lijstr.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The base Lijstr Spring application Exception.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class LijstrException extends RuntimeException {

    /**
     * {@inheritDoc}.
     */
    public LijstrException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}.
     */
    public LijstrException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}.
     */
    public LijstrException(Throwable cause) {
        super(cause);
    }

}
