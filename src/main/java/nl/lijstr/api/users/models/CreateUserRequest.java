package nl.lijstr.api.users.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.lijstr.domain.other.ApprovedFor;
import org.hibernate.validator.constraints.Length;

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

    private ApprovedFor approvedFor;

}
