package nl.lijstr.api.errors;

import nl.lijstr.api.errors.models.ValidationErrors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.validation.ConstraintViolationException;

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

    /**
     * Called when a ValidationError occurs.
     *
     * @param e The error
     *
     * @return list of validation errors
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationErrors validationError(MethodArgumentNotValidException e) {
        return new ValidationErrors(
                e.getBindingResult().getAllErrors()
        );
    }

}
