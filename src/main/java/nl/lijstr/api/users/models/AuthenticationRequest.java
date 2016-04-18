package nl.lijstr.api.users.models;

import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Stoux on 18/04/2016.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    private boolean rememberMe;

}
