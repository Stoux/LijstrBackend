package nl.lijstr.security.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

/**
 * A simple {@link GrantedAuthority} that substitutes for {@link nl.lijstr.domain.users.GrantedPermission}
 * in JSON Web Tokens context.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JwtGrantedAuthority implements GrantedAuthority {

    private String permission;

    @Override
    public String getAuthority() {
        return permission;
    }

}
