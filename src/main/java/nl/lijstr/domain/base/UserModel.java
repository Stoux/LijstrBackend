package nl.lijstr.domain.base;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import nl.lijstr.domain.users.User;

/**
 * An extended IdModel that is linked to a User.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class UserModel extends IdModel {

    @ManyToOne
    private User user;

}
