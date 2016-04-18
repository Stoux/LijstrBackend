package nl.lijstr.security.model;

import lombok.*;
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
