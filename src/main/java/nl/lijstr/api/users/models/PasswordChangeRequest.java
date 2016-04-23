package nl.lijstr.api.users.models;

import javax.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Leon Stam on 21-4-2016.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequest {

    @Size(min = 5)
    @NotEmpty
    private String currentPassword;

    @Size(min = 5)
    @NotEmpty
    private String newPassword;

}
