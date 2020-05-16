package nl.lijstr.repositories.users;

import nl.lijstr.domain.users.EmailSettings;
import nl.lijstr.repositories.abs.BasicRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * The basic {@link EmailSettings} repository.
 */
public interface EmailSettingsRepository extends BasicRepository<EmailSettings> {

    /**
     * Find all email settings that should get an update today.
     *
     * @param date the date to match against
     *
     * @return list of email settings
     */
    List<EmailSettings> findAllByNextScheduledUpdateIsLessThanEqual(LocalDate date);

    /**
     * Attempt to find a user's email settings.
     *
     * @param userId ID of the user
     *
     * @return settings if found
     */
    Optional<EmailSettings> findByUserId(long userId);


}
