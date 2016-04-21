package nl.lijstr.api.users.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Leon Stam on 21-4-2016.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotEmpty
    private String username;
    @NotEmpty
    private String email;

}
