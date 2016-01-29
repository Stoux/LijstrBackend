package nl.lijstr.domain.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.persistence.*;
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

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String displayName;

    @Column(unique = true)
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

    @SuppressWarnings("squid:UnusedPrivateMethod")
    @PrePersist
    @PreUpdate
    private void fillPassword() {
        if (password != null) {
            //TODO: Improve
            setHashedPassword(new StringBuilder(password).reverse().toString());
        }
    }

}
