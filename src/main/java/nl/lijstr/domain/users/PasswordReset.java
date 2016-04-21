package nl.lijstr.domain.users;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.*;
import nl.lijstr.domain.base.IdCmUserModel;
import nl.lijstr.domain.base.IdUserModel;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PasswordReset extends IdCmUserModel {

    private String remoteAddress;
    private Integer usedPort;
    private String userAgent;

    @Column(unique = true)
    private String resetToken;

    private LocalDateTime expires;
    private boolean used;

}
