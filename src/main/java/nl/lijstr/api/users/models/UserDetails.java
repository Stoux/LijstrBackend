package nl.lijstr.api.users.models;

import lombok.Getter;
import lombok.NonNull;
import nl.lijstr.domain.users.LoginAttempt;
import nl.lijstr.domain.users.User;

@Getter
public class UserDetails {

    @NonNull
    private User user;
    private LoginAttempt lastLogin;
    private LoginAttempt lastFailedLoginAttempt;

    public UserDetails(User user, LoginAttempt lastLogin, LoginAttempt lastFailedLoginAttempt) {
        this.user = user;
        this.lastLogin = lastLogin;
        this.lastFailedLoginAttempt = lastFailedLoginAttempt;
    }

}
