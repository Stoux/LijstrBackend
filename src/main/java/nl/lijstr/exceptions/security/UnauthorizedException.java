package nl.lijstr.exceptions.security;

import nl.lijstr.exceptions.LijstrException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An Exception that indacates someone isn't authorized to access something.
 * Often thrown when a User doesn't have the correct rights or simply isn't logged in.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends LijstrException {

    /**
     * Create an {@link UnauthorizedException}.
     */
    public UnauthorizedException() {
        super("Unauthorized");
    }

}
