package nl.lijstr.api.errors.models;

import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/**
 * Created by Stoux on 21/04/2016.
 */
@Getter
public class ValidationErrors {

    private int totalErrors;
    private List<Object> errors;

    /**
     * Create a {@link ValidationErrors} object based on a list of {@link ObjectError}s.
     *
     * @param objectErrors The errors
     */
    public ValidationErrors(List<ObjectError> objectErrors) {
        this.totalErrors = objectErrors.size();
        this.errors = new ArrayList<>(totalErrors);

        for (ObjectError objectError : objectErrors) {
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                this.errors.add(new ValidationFieldError(
                        fieldError.getField(), fieldError.getDefaultMessage(), fieldError.getRejectedValue()
                ));
            } else {
                this.errors.add(new ValidationError(
                        objectError.getDefaultMessage()
                ));
            }
        }
    }

    @Getter
    @AllArgsConstructor
    private class ValidationFieldError {
        private String field;
        private String message;
        private Object rejectedValue;
    }

    @Getter
    @AllArgsConstructor
    private class ValidationError {
        private String message;
    }

}
