package nl.lijstr.domain.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.lijstr.domain.base.IdUserModel;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"user"})
public class LoginAttempt extends IdUserModel {

    private LocalDateTime timestamp;
    private String remoteAddress;
    private Integer usedPort;
    private String userAgent;

    private String username;
    private boolean success;
    private String rejectionReason;

    @Enumerated(EnumType.ORDINAL)
    private Type loginType;

    /**
     * Create a new {@link LoginAttempt} that is expected to be successful.
     *
     * @param remoteAddress The remote IP
     * @param usedPort      The remote port
     * @param userAgent     The user agent
     * @param username      The username
     * @param loginType     Type of login
     */
    public LoginAttempt(String remoteAddress, Integer usedPort, String userAgent, String username, Type loginType) {
        this.remoteAddress = remoteAddress;
        this.usedPort = usedPort;
        this.userAgent = userAgent;
        this.username = username;
        this.loginType = loginType;

        this.timestamp = LocalDateTime.now();
        this.success = true;
        this.rejectionReason = null;
    }

    /**
     * Change the loginAttempt to failed (success = false).
     *
     * @param rejectionReason The reason
     */
    public void fail(String rejectionReason) {
        this.success = false;
        this.rejectionReason = rejectionReason;
    }

    /**
     * Enum with possible types of login.
     */
    public enum Type {
        AUTHENTICATION,
        TOKEN_REFRESH
    }

}
