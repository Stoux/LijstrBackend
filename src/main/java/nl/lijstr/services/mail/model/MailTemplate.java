package nl.lijstr.services.mail.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Stoux on 22/04/2016.
 */
@Getter
@AllArgsConstructor
public class MailTemplate {

    /**
     * The mail's main message.
     */
    private String message;

    /**
     * The URL path after the host.
     */
    private String buttonUrlPath;

    /**
     * The button text.
     */
    private String button;

}
