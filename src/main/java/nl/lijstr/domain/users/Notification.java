package nl.lijstr.domain.users;

import javax.persistence.Entity;
import lombok.*;
import nl.lijstr.domain.base.IdUserModel;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Notification extends IdUserModel {

    private String section;


}
