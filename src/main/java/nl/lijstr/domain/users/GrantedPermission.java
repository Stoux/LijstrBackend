package nl.lijstr.domain.users;

import javax.persistence.Entity;
import lombok.*;
import nl.lijstr.domain.base.IdCmUserModel;
import org.springframework.security.core.GrantedAuthority;

/**
 * Created by Stoux on 26/01/2016.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class GrantedPermission extends IdCmUserModel implements GrantedAuthority {

    private String permission;

    @Override
    public String getAuthority() {
        return permission;
    }

}
