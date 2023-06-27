package nl.lijstr.api.users;

import nl.lijstr.api.abs.AbsService;
import nl.lijstr.api.users.models.NewPasswordRequest;
import nl.lijstr.api.users.models.ResetPasswordRequest;
import nl.lijstr.beans.PasswordBean;
import nl.lijstr.domain.users.PasswordReset;
import nl.lijstr.services.mail.MailService;
import nl.lijstr.services.mail.model.MailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Created by Stoux on 23/04/2016.
 */
@RestController
@RequestMapping(value = "/auth/resetPassword", produces = "application/json")
public class PasswordResetEndpoint extends AbsService {

    @Autowired
    private PasswordBean passwordBean;

    @Autowired
    private MailService mailService;

    /**
     * Request a password to be reset.
     *
     * @param resetRequest  The user details
     * @param springRequest The spring request
     */
    @RequestMapping(method = RequestMethod.POST)
    public void requestPasswordReset(@Valid @RequestBody ResetPasswordRequest resetRequest,
                                     HttpServletRequest springRequest) {
        PasswordReset passwordReset = passwordBean.requestPasswordReset(
                resetRequest.getUsername(), resetRequest.getEmail(), springRequest
        );

        mailService.sendMail(
                "Password reset",
                passwordReset.getUser(),
                new MailTemplate(
                        "Je hebt een wachtwoord reset aangevraagd. Druk op die knop om een nieuw wachtwoord in te vullen.",
                        "/dashboard/reset-password?token=" + passwordReset.getResetToken(),
                        "Deze knop ja"
                ),
                "password-reset"
        );
    }

    /**
     * Reset a password using a reset token.
     *
     * @param newPassword The new password details
     */
    @RequestMapping(method = RequestMethod.PUT)
    public void resetPassword(@Valid @RequestBody NewPasswordRequest newPassword) {
        passwordBean.resetPassword(newPassword.getResetToken(), newPassword.getNewPassword());
    }

}
