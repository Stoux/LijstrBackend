package nl.lijstr.domain.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import lombok.*;
import nl.lijstr.domain.base.IdCmModel;
import nl.lijstr.services.modify.annotations.NotModifiable;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User extends IdCmModel {

    @Column(unique = true)
    @NotModifiable
    private String username;

    @NotModifiable
    @JsonIgnore
    @NotNull
    private String hashedPassword;

    private String displayName;

    @Column(unique = true)
    private String email;

    //Has an avatar
    private boolean avatar;

    //Validating password key, used to invalidate JSON Web Tokens
    private int validatingKey;

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

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<GrantedPermission> grantedPermissions;

    /**
     * Create a User by it's ID.
     * This is intended to be used as a reference object (for relations).
     *
     * @param id The ID
     */
    public User(long id) {
        this.id = id;
    }

}
