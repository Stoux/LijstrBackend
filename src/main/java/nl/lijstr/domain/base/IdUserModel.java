package nl.lijstr.domain.base;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import lombok.*;
import nl.lijstr.domain.users.User;

/**
 * An extended IdModel that is linked to a User.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class IdUserModel extends IdModel {

    @ManyToOne
    private User user;

}
