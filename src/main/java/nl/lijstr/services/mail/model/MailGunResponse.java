package nl.lijstr.services.mail.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Stoux on 22/04/2016.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MailGunResponse {

    private String id;
    private String message;

}
