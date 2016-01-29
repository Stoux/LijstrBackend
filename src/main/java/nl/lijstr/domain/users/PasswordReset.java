package nl.lijstr.domain.users;

import javax.persistence.Entity;
import lombok.*;
import nl.lijstr.domain.base.UserModel;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
@Setter
@AllArgsConstructor
@Entity
public class PasswordReset extends UserModel {

}
