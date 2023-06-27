package nl.lijstr.domain.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.lijstr.domain.base.IdCmModel;
import nl.lijstr.domain.other.ApprovedFor;
import nl.lijstr.services.modify.annotations.NotModifiable;

import jakarta.persistence.*;

import java.util.List;

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

    @Column(nullable = false)
    @NotModifiable
    @JsonIgnore
    private String hashedPassword;

    private String displayName;

    @Column(unique = true)
    private String email;

    //Has an avatar
    private boolean avatar;

    //Validating password key, used to invalidate JSON Web Tokens
    @JsonIgnore
    private int validatingKey;

    @Enumerated(EnumType.ORDINAL)
    private ApprovedFor approvedFor;

    @Column
    private String oldSiteUser;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GrantedPermission> grantedPermissions;

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private EmailSettings emailSettings;

    /**
     * Create a User by it's ID.
     * This is intended to be used as a reference object (for relations).
     *
     * @param id The ID
     */
    public User(long id) {
        this.id = id;
    }

    /**
     * Create a new User.
     *
     * @param username    The username
     * @param displayName The displayName
     * @param email       The email
     * @param approvedFor Approved for
     */
    public User(String username, String displayName, String email, ApprovedFor approvedFor) {
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.approvedFor = approvedFor;

        this.avatar = false;
        this.validatingKey = 0;
    }

    /**
     * Increment the validating key.
     */
    public void incrementValidatingKey() {
        validatingKey++;
    }

}
