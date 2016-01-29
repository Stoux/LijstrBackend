package nl.lijstr.domain.base;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import lombok.*;
import nl.lijstr.domain.users.User;

/**
 * An IdCmModel which is linked to a User.
 * A user can be linked to multiple of these.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class IdCmUserModel extends IdCmModel {

    @ManyToOne
    private User user;

}
