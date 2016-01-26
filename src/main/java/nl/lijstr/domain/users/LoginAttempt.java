package nl.lijstr.domain.users;

import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nl.lijstr.domain.base.UserModel;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
public class LoginAttempt extends UserModel {
}
