package nl.lijstr.api.users;

import nl.lijstr.api.abs.AbsService;
import nl.lijstr.domain.users.EmailSettings;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.exceptions.BadRequestException;
import nl.lijstr.exceptions.db.NotFoundException;
import nl.lijstr.repositories.users.EmailSettingsRepository;
import nl.lijstr.security.model.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Endpoint for fetching & updating a user's email settings.
 */
@Validated
@RestController
@RequestMapping(value = "/users/email-settings", produces = "application/json")
public class EmailSettingsEndpoint extends AbsService {

    @Autowired
    private EmailSettingsRepository repository;

    /**
     * Allow a user to fetch their own settings.
     *
     * @return the settings
     */
    @Secured(Permission.MOVIE_USER)
    @RequestMapping("/me")
    public EmailSettings getOwnSettings() {
        final JwtUser user = getUser();
        final Long userId = user.getId();
        return getEmailSettingsForUser(userId);
    }

    /**
     * Calculate the next update date for the user based on the passed settings.
     *
     * @param updateInterval Requested update interval
     * @param dayOfWeek Optional day of the week (required for !monthly)
     * @param dayOfMonth Optional day of the month
     *
     * @return the date
     */
    @Secured(Permission.MOVIE_USER)
    @RequestMapping("/me/calculate")
    public LocalDate calculate(
            @RequestParam() final EmailSettings.Interval updateInterval,
        @RequestParam(required = false) final DayOfWeek dayOfWeek,
        @RequestParam(required = false) @Min(1) @Max(28) final Integer dayOfMonth
    ) {
        // Validate params
        validateSettingArguments(updateInterval, dayOfWeek, dayOfMonth);
        final EmailSettings settings = fetchCurrentUserEmailSettings();

        final LocalDate newDate = EmailSettings.determineNextDate(
            LocalDate.now(),
            settings.getLastUpdate().toLocalDate(),
            updateInterval,
            dayOfMonth,
            dayOfWeek
        );

        return newDate;
    }

    /**
     * Modify the next update date for the user based on the passed settings.
     *
     * @param updateInterval Requested update interval
     * @param dayOfWeek Optional day of the week (required for !monthly)
     * @param dayOfMonth Optional day of the month
     *
     * @return updated settings
     */
    @Secured(Permission.MOVIE_USER)
    @RequestMapping("/me/update")
    public EmailSettings update(
        @RequestParam() final EmailSettings.Interval updateInterval,
        @RequestParam(required = false) final DayOfWeek dayOfWeek,
        @RequestParam(required = false) @Min(1) @Max(28) final Integer dayOfMonth
    ) {
        validateSettingArguments(updateInterval, dayOfWeek, dayOfMonth);
        final EmailSettings settings = fetchCurrentUserEmailSettings();

        settings.setUpdateInterval(updateInterval);
        settings.setPreferredDayOfMonth(dayOfMonth);
        settings.setPreferredDayOfWeek(dayOfWeek);

        // Calculate the new date
        final LocalDate newDate = EmailSettings.determineNextDate(
            LocalDate.now(),
            settings.getLastUpdate().toLocalDate(),
            updateInterval,
            dayOfMonth,
            dayOfWeek
        );

        // Save it
        settings.setNextScheduledUpdate(newDate);
        return this.repository.save(settings);
    }

    private void validateSettingArguments(final EmailSettings.Interval updateInterval, final DayOfWeek dayOfWeek, final Integer dayOfMonth) {
        if ((dayOfMonth != null && dayOfWeek != null) || (dayOfMonth != null && updateInterval != EmailSettings.Interval.MONTHLY)) {
            throw new BadRequestException("Invalid day arguments");
        }

        if (dayOfMonth != null && ( dayOfMonth < 1 || dayOfMonth > 28 ) ) {
            throw new BadRequestException("Incorrect day of month (min: 1, max: 28)");
        }
    }

    private EmailSettings fetchCurrentUserEmailSettings() {
        final JwtUser user = getUser();
        final Long userId = user.getId();
        return getEmailSettingsForUser(userId);
    }

    /**
     * Allow an admin to fetch another user's settings.
     *
     * @param id ID of the user
     *
     * @return the settings
     */
    @Secured(Permission.ADMIN)
    @RequestMapping("/{id}")
    public EmailSettings getUserSettings(@PathVariable("id") final long id) {
        return getEmailSettingsForUser(id);
    }

    private EmailSettings getEmailSettingsForUser(Long userId) {
        final Optional<EmailSettings> optSettings = this.repository.findByUserId(userId);
        if (optSettings.isPresent()) {
            return optSettings.get();
        } else {
            throw new NotFoundException("Email settings", userId);
        }
    }


}
