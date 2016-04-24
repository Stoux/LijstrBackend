package nl.lijstr.beans;

import nl.lijstr.security.model.JwtUser;
import nl.lijstr.security.spring.JwtAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Created by Stoux on 24/04/2016.
 */
@Component
public class UserBean {

    /**
     * Get the logged in JwtUser.
     * This will throw a runtime exception if there's no user or if the user isn't a JwtUser.
     *
     * @return the user
     */
    public JwtUser getJwtUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication instanceof JwtAuthenticationToken) {
            return ((JwtAuthenticationToken) authentication).getJwtUser();
        } else {
            throw new AuthenticationCredentialsNotFoundException("No JSON Web Tokens user found");
        }
    }

}
