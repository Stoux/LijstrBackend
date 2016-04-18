package nl.lijstr.exceptions.security;

import nl.lijstr.exceptions.LijstrException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Stoux on 18/04/2016.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenExpiredException extends LijstrException {

    public TokenExpiredException() {
        super("The token has expired.");
    }
}
