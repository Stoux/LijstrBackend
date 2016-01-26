package nl.lijstr.domain.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.*;
import nl.lijstr.domain.base.IdCmModel;
import nl.lijstr.services.modify.annotations.NotModifiable;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User extends IdCmModel {

    @NotModifiable
    private String username;
    private String password;
    private String displayName;
    private String email;

    //Has an avatar
    private boolean avatar;


    //Relations
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Notification> notifications;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<LoginAttempt> loginAttempts;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<PasswordReset> passwordResets;


}
