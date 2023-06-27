package nl.lijstr.api.users.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.lijstr.domain.users.User;
import org.hibernate.validator.constraints.Length;

/**
 * A model used for updating users.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Length(min = 3)
    private String username;

    @NotEmpty
    private String displayName;

    @NotEmpty
    @Email
    private String email;

    /**
     * Apply this update to the given user.
     * @param user
     */
    public void applyTo(User user) {
        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setEmail(email);
    }

}

