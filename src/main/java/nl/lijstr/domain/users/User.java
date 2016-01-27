package nl.lijstr.domain.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.persistence.*;
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

    @Column(unique = true)
    @NotModifiable
    private String username;
    @JsonIgnore
    private String password;
    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String plainPassword;
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
    private void fillPassword() {
        if (plainPassword != null) {
            //TODO: Improve
            setPassword(new StringBuilder(plainPassword).reverse().toString());
        }
    }

}
