package nl.lijstr.emails;

import io.sentry.Sentry;
import nl.lijstr.api.abs.AbsService;
import nl.lijstr.domain.users.EmailSettings;
import nl.lijstr.domain.users.GrantedPermission;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.BadRequestException;
import nl.lijstr.processors.annotations.InjectLogger;
import nl.lijstr.repositories.users.EmailSettingsRepository;
import nl.lijstr.repositories.users.UserRepository;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Email scheduler that sends digests to users about recent changes.
 * Doubles as a {@link RestController} for debug (admin) endpoints.
 */
@RestController
public class EmailScheduler extends AbsService {

    @InjectLogger
    private Logger logger;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailSettingsRepository emailSettingsRepository;

    @Autowired
    private DigestMailer digestMailer;

    /**
     * Debug endpoint for force sending an email to the given user.
     * @param userId ID of the user
     */
    @Secured(Permission.ADMIN)
    @RequestMapping(path = "/users/{userId:\\d+}/send-email", method = RequestMethod.PUT)
    public void sendEmailToUser(final @PathVariable() Long userId) {
        final User user = findOne(userRepository, userId, "User");
        if (!hasMovieUserPermission(user)) {
            throw new BadRequestException("User isn't a MOVIE_USER");
        }

        final Optional<EmailSettings> optSettings = emailSettingsRepository.findByUserId(user.getId());
        if (optSettings.isPresent()) {
            sendUpdate(optSettings.get());
        } else {
            throw new BadRequestException("No email settings found for user");
        }
    }

    /**
     * Cronjob for sending digest emails to all users.
     */
    @Transactional
    @Scheduled(cron = "0 45 11 * * *")
    public void sendMails() {
        this.logger.info("Starting digest sender");

        // Init any missing settings for the users that don't have one yet.
        initMissingEmailSettings();

        // Find all that should get an update today
        final List<EmailSettings> updates = emailSettingsRepository.findAllByNextScheduledUpdateIsLessThanEqual(LocalDate.now());
        for (final EmailSettings update : updates) {
            // Make sure the user still is a movie user.
            final User user = update.getUser();
            if (hasMovieUserPermission(user)) {
                sendUpdate(update);
            }
        }
    }

    private boolean hasMovieUserPermission(User user) {
        final Optional<GrantedPermission> foundPermission = user.getGrantedPermissions().stream()
            .filter(gp -> gp.getPermission().getName().equals(Permission.MOVIE_USER))
            .findFirst();
        return foundPermission.isPresent();
    }


    private void sendUpdate(final EmailSettings emailSettings) {
        final User user = emailSettings.getUser();

        // Send the mail
        this.logger.info(String.format("Sending movie digest to %s", user.getUsername()));
        try {
            this.digestMailer.sendDigestTo(user, emailSettings.getLastUpdate());
        } catch (Exception e) {
            // Something went wrong
            this.logger.error(String.format("Failed to send digest to %s: %s", user.getUsername(), e.getMessage()));
            Sentry.captureException(e);
            return;
        }

        // Last update was today
        final LocalDateTime previousDate = emailSettings.getLastUpdate();
        emailSettings.setLastUpdate(LocalDateTime.now());

        // Determine the next update date
        final LocalDate nextDate = EmailSettings.determineNextDate(
            LocalDate.now(),
            previousDate.toLocalDate(),
            emailSettings.getUpdateInterval(),
            emailSettings.getPreferredDayOfMonth(),
            emailSettings.getPreferredDayOfWeek()
        );
        emailSettings.setNextScheduledUpdate(nextDate);
        this.emailSettingsRepository.save(emailSettings);
        this.logger.info("Done! Next mail scheduled for " + nextDate.toString());
    }

    /**
     * Init the email settings for any users that are missing them.
     * Also setup as a debug endpoint.
     */
    @Secured(Permission.ADMIN)
    @RequestMapping(path = "/emails/init-missing-settings", method = RequestMethod.POST)
    public void initMissingEmailSettings() {
        // Find any users that have no email settings yet.
        final List<User> users = userRepository.findByEmailSettingsIsNullAndGrantedPermissionsPermissionName(Permission.MOVIE_USER);
        for (final User user : users) {
            this.logger.info("Creating email settings for user: " + user.getDisplayName());
            final EmailSettings emailSettings = EmailSettings.createDefault(user);
            user.setEmailSettings(emailSettings);
            userRepository.save(user);
        }
    }

}
