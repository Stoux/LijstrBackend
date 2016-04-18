package nl.lijstr.exceptions.security;

import nl.lijstr.exceptions.LijstrException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Leon Stam on 18-4-2016.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessExpiredException extends LijstrException {

    /**
     * Create a new {@link AccessExpiredException}.
     */
    public AccessExpiredException() {
        super("The access token has expired. Please refresh the token.");
    }

}
