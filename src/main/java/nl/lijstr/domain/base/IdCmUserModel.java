package nl.lijstr.domain.base;

import lombok.Getter;
import lombok.Setter;
import nl.lijstr.domain.users.User;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

/**
 * An IdCmModel which is linked to a User.
 * A user can be linked to multiple of these.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class IdCmUserModel extends IdCmModel {

    @ManyToOne
    protected User user;

}
