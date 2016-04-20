package nl.lijstr.domain.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.*;
import nl.lijstr.domain.base.IdCmUserModel;
import org.springframework.security.core.GrantedAuthority;

/**
 * Created by Stoux on 26/01/2016.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"user"})
public class GrantedPermission extends IdCmUserModel implements GrantedAuthority {

    @JsonIgnore
    @ManyToOne
    private Permission permission;

    /**
     * Create a new {@link GrantedPermission} that is assigned to a {@link User}.
     *
     * @param user       The user
     * @param permission The permission
     */
    public GrantedPermission(User user, Permission permission) {
        this.user = user;
        this.permission = permission;
    }

    @Override
    public String getAuthority() {
        return permission.toString();
    }

}
