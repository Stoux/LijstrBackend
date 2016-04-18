package nl.lijstr.exceptions.security;

import nl.lijstr.exceptions.LijstrException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Stoux on 18/04/2016.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenExpiredException extends LijstrException {

    /**
     * Create a {@link TokenExpiredException}.
     * This is often used when an JSON Web Token has expired.
     */
    public TokenExpiredException() {
        super("The token has expired.");
    }
}
