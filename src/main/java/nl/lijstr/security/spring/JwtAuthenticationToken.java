package nl.lijstr.security.spring;

import lombok.Getter;
import nl.lijstr.security.model.JwtUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * Created by Stoux on 19/04/2016.
 */
@Getter
public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private JwtUser jwtUser;

    /**
     * Create a new {@link UsernamePasswordAuthenticationToken} from a {@link JwtUser}.
     *
     * @param jwtUser The user
     */
    public JwtAuthenticationToken(JwtUser jwtUser) {
        super(jwtUser.getUsername(), null, jwtUser.getAuthorities());
        this.jwtUser = jwtUser;
    }

}
