package nl.lijstr.exceptions.security;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by Stoux on 18/04/2016.
 */
public class TokenExpiredException extends AuthenticationException {

    /**
     * Create a {@link TokenExpiredException}.
     * This is often used when an JSON Web Token has expired.
     */
    public TokenExpiredException() {
        super("The token has expired.");
    }

}
