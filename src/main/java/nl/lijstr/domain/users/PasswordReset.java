package nl.lijstr.domain.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.lijstr.domain.base.IdCmUserModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

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
