package nl.lijstr.exceptions.db;

import nl.lijstr.exceptions.LijstrException;

/**
 * Created by Stoux on 27/01/2016.
 */
public class InvalidEntityException extends LijstrException {

    /**
     * Create an InvalidEntityException.
     *
     * @param incorrectField The field that's incorrect
     * @param reason         The reason why it's incorrect
     */
    public InvalidEntityException(String incorrectField, String reason) {
        super("The value of field '" + incorrectField + "' is incorrect. " + reason);
    }

}
