package nl.lijstr.api.users.models;

import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * A model used for creating new users.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @Length(min = 3)
    private String username;

    @NotEmpty
    private String displayName;

    @NotEmpty
    @Email
    private String email;

}
