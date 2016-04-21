package nl.lijstr.api.users.models;

import javax.validation.constraints.Size;
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
public class NewPasswordRequest {

    @NotEmpty
    private String resetToken;

    @Size(min = 5)
    @NotEmpty
    private String newPassword;

}
