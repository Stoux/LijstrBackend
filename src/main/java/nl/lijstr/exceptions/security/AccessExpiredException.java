package nl.lijstr.exceptions.security;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by Leon Stam on 18-4-2016.
 */
public class AccessExpiredException extends AuthenticationException {

    /**
     * Create a new {@link AccessExpiredException}.
     */
    public AccessExpiredException() {
        super("The access token has expired. Please refresh the token.");
    }

}
