package nl.lijstr.domain.base;

import lombok.Getter;
import lombok.Setter;
import nl.lijstr.domain.users.User;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

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
