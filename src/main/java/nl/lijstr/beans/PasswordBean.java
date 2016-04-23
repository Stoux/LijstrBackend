package nl.lijstr.beans;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import javax.servlet.http.HttpServletRequest;
import nl.lijstr.domain.users.PasswordReset;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.BadRequestException;
import nl.lijstr.exceptions.db.ConflictException;
import nl.lijstr.exceptions.db.NotFoundException;
import nl.lijstr.repositories.users.PasswordResetRepository;
import nl.lijstr.repositories.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Leon Stam on 21-4-2016.
 */
@Component
public class PasswordBean {

    /**
     * The number of minutes a {@link nl.lijstr.domain.users.PasswordReset} is valid.
     */
    public static final long PASSWORD_RESET_TIME = 30L;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecureRandom secureRandom;

    /**
     * Request a password to be reset.
     *
     * @param username      The username
     * @param email         The email
     * @param springRequest The spring springRequest (for logging)
     *
     * @return the reset object
     */
    public PasswordReset requestPasswordReset(String username, String email, HttpServletRequest springRequest) {
        //Find the user
        final User user = userRepository.findByUsernameAndEmail(username, email);
        if (user == null) {
            throw new NotFoundException("No user found with this username & email");
        }

        //Check for any open password resets
        PasswordReset passwordReset = passwordResetRepository.findFirstByUserOrderByCreatedDesc(user);
        if (passwordReset != null) {
            if (!passwordReset.isUsed() && passwordReset.getExpires().isAfter(LocalDateTime.now())) {
                throw new ConflictException("Previous password reset hasn't expired yet");
            }
        }

        //Create a new one
        String randomId = new BigInteger(256, secureRandom).toString(64);
        PasswordReset newReset = new PasswordReset(
                springRequest.getRemoteAddr(), springRequest.getRemotePort(),
                springRequest.getHeader("user-agent"),
                randomId,
                LocalDateTime.now().plusMinutes(PASSWORD_RESET_TIME),
                false
        );
        newReset.setUser(user);

        return passwordResetRepository.save(newReset);
    }

    /**
     * Reset a user's password.
     *
     * @param token       The reset token
     * @param newPassword The new password
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        //Find the PasswordReset & check if valid
        PasswordReset reset = passwordResetRepository.findByResetToken(token);
        if (reset == null) {
            throw new NotFoundException("Password Reset", token);
        }
        if (reset.isUsed()) {
            throw new BadRequestException("This token is already used");
        }
        if (reset.getExpires().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token has expired");
        }

        //Hash the password
        reset.setUsed(true);
        passwordResetRepository.save(reset);

        User user = reset.getUser();
        user.setHashedPassword(passwordEncoder.encode(newPassword));
        user.setValidatingKey(user.getValidatingKey() + 1);
        userRepository.save(user);
    }


}
