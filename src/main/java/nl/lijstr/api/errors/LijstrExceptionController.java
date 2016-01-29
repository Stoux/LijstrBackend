package nl.lijstr.api.errors;

import javax.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Stoux on 29/01/2016.
 */
@ControllerAdvice
public class LijstrExceptionController {

    /**
     * Called when a Conflict of data occurs.
     */
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Data integrity violation")
    @ExceptionHandler({DataIntegrityViolationException.class, ConstraintViolationException.class})
    public void conflict() {

    }


}
