package nl.lijstr.exceptions.api;

import nl.lijstr.exceptions.LijstrException;

/**
 * Exception triggered by an error from an external API.
 */
public class ExternalApiException extends LijstrException {

    public ExternalApiException(String message) {
        super(message);
    }

    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExternalApiException(Throwable cause) {
        super(cause);
    }

}
