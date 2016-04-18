package nl.lijstr.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Leon Stam on 18-4-2016.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends LijstrException {

    public BadRequestException(String message) {
        super(message);
    }
}

