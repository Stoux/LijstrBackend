package nl.lijstr.repositories.users;

import java.time.LocalDateTime;
import java.util.List;
import nl.lijstr.domain.users.LoginAttempt;
import nl.lijstr.repositories.abs.BasicRepository;

/**
 * Created by Stoux on 20/04/2016.
 */
public interface LoginAttemptRepository extends BasicRepository<LoginAttempt> {

    /**
     * Find all logins attempt by their address and after a certain timestamp.
     *
     * @param remoteAddress The remote address
     * @param success       If the login attempt was successful
     * @param loginType     The login type
     * @param after         After a certain time
     *
     * @return The list of logins
     */
    List<LoginAttempt> findByRemoteAddressAndSuccessAndLoginTypeAndTimestampAfterOrderByTimestampAsc(
            String remoteAddress, boolean success, LoginAttempt.Type loginType, LocalDateTime after
    );

}
