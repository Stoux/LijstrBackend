package nl.lijstr.repositories.users;

import nl.lijstr.domain.users.PasswordReset;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.abs.BasicRepository;

/**
 * The basic {@link PasswordReset} repository.
 */
public interface PasswordResetRepository extends BasicRepository<PasswordReset> {

    /**
     * Find a {@link PasswordReset} by it's token.
     *
     * @param token The token
     *
     * @return the passwordReset
     */
    PasswordReset findByResetToken(String token);

    /**
     * Find the latest {@link PasswordReset} a user has made.
     *
     * @param user The user
     *
     * @return the passwordReset or null
     */
    PasswordReset findFirstByUserOrderByCreatedDesc(User user);

}
