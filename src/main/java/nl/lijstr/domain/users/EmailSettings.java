package nl.lijstr.domain.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.sentry.Sentry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.lijstr.domain.base.IdModel;
import nl.lijstr.exceptions.LijstrException;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Optional;

/**
 * Model specifying the user's email settings.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = EmailSettings.TABLE)
@Entity
public class EmailSettings extends IdModel {

    public static final String TABLE = "user_email_settings";

    @JsonIgnore
    @OneToOne
    private User user;

    private LocalDateTime lastUpdate;
    private LocalDate nextScheduledUpdate;


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Interval updateInterval;
    private Integer preferredDayOfMonth;
    @Enumerated(EnumType.STRING)
    private DayOfWeek preferredDayOfWeek;

    /**
     * Interval at which the mail should be send.
     */
    public enum Interval {
//        BIWEEKLY,
        WEEKLY,
        FORTNIGHT /* Every two weeks */,
        MONTHLY,
//        NEVER,
    }

    /**
     * Create the default email settings for the given user.
     *
     * @param user the user
     *
     * @return the settings
     */
    public static EmailSettings createDefault(final User user) {
        return new EmailSettings(
            user,
            LocalDateTime.now().minusDays(7),
            LocalDate.now(),
            EmailSettings.Interval.WEEKLY,
            null,
            DayOfWeek.MONDAY
        );
    }

    /**
     * Deterime the next date for an update.
     *
     * @param requiredAfter The required minimum date (most often today)
     * @param previousDate The previous update the user received (important for non weekly/monthly items)
     * @param interval Interval at which the update should be received
     * @param preferredDayOfMonth Preferred day of the month (if using MONTHLY)
     * @param preferredDayOfWeek Preferred day of the week
     *
     * @return the next date
     */
    public static LocalDate determineNextDate(
        final LocalDate requiredAfter,
        final LocalDate previousDate,
        final Interval interval,
        final Integer preferredDayOfMonth,
        final DayOfWeek preferredDayOfWeek
    ) {

        switch (interval) {
            case WEEKLY:
                assert preferredDayOfWeek != null;
                return determineForWeekly(preferredDayOfWeek, requiredAfter);

            case FORTNIGHT:
                return determineForFortnight(preferredDayOfWeek, requiredAfter, previousDate);

            case MONTHLY:
                return determineForMonthly(preferredDayOfMonth, preferredDayOfWeek, requiredAfter);

            default:
                throw new LijstrException("Invalid usage of method!");
        }
    }

    private static LocalDate determineForWeekly(final DayOfWeek preferredDayOfWeek, final LocalDate requiredAfter) {
        // Try current week
        final LocalDate potentialFirstDayOfWeek = requiredAfter.with(WeekFields.ISO.dayOfWeek(), preferredDayOfWeek.getValue());
        if (potentialFirstDayOfWeek.isAfter(requiredAfter)) {
            return potentialFirstDayOfWeek;
        } else {
            // Try next week
            return requiredAfter.plusWeeks(1).with(WeekFields.ISO.dayOfWeek(), preferredDayOfWeek.getValue());
        }
    }

    private static LocalDate determineForFortnight(final DayOfWeek preferredDayOfWeek, final LocalDate requiredAfter, final LocalDate previousDate) {
        final TemporalField dayOfWeek = WeekFields.ISO.dayOfWeek();

        LocalDate foundDate = requiredAfter.with(dayOfWeek, preferredDayOfWeek.getValue());
        // Make sure the foundDate is after the required date && that there at least two weeks between the dates
        while(!foundDate.isAfter(requiredAfter) || ChronoUnit.WEEKS.between(previousDate, foundDate) < 2) {
            // Add a week + set it back to the preferred day of that week
            foundDate = foundDate.plusWeeks(1).with(dayOfWeek, preferredDayOfWeek.getValue());
        }

        return foundDate;
    }



    private static LocalDate determineForMonthly(Integer preferredDayOfMonth, DayOfWeek preferredDayOfWeek, LocalDate requiredAfter) {
        final YearMonth currentYearMonth = YearMonth.of(requiredAfter.getYear(), requiredAfter.getMonth());
        if (preferredDayOfMonth != null) {
            // Check if the next preferred day of the month is still available this month
            final LocalDate nextPreferredDayOfMonth = currentYearMonth.atDay(preferredDayOfMonth);
            if (nextPreferredDayOfMonth.isAfter(requiredAfter)) {
                // It is available
                return nextPreferredDayOfMonth;
            } else {
                // Otherwise get the next month
                return currentYearMonth.plusMonths(1).atDay(preferredDayOfMonth);
            }
        } else {
            // Try in this month
            final Optional<LocalDate> foundFirstDate = determineFirstAvailableDayOfMonth(
                currentYearMonth.atDay(1), preferredDayOfWeek, requiredAfter
            );
            if (foundFirstDate.isPresent()) {
                return foundFirstDate.get();
            }

            // Try next month
            final Optional<LocalDate> nextMonthFirstDate = determineFirstAvailableDayOfMonth(
                currentYearMonth.plusMonths(1).atDay(1), preferredDayOfWeek, requiredAfter
            );
            if (nextMonthFirstDate.isPresent()) {
                return nextMonthFirstDate.get();
            } else {
                Sentry.capture("This shouldn't happen. Unable to resolve a date for monthly, preferred day of week: " + preferredDayOfWeek.name());
                return requiredAfter.plusMonths(1);
            }
        }
    }

    private static Optional<LocalDate> determineFirstAvailableDayOfMonth(final LocalDate firstDayOfMonth, final DayOfWeek preferredDayOfWeek, final LocalDate requiredAfter) {
        LocalDate potentialFirstDayOfWeek = firstDayOfMonth.with(WeekFields.ISO.dayOfWeek(), preferredDayOfWeek.getValue());
        if ( potentialFirstDayOfWeek.isBefore( firstDayOfMonth )) {
            // Try 1 week later
            potentialFirstDayOfWeek = potentialFirstDayOfWeek.plusWeeks(1).with(WeekFields.ISO.dayOfWeek(), preferredDayOfWeek.getValue());
        }

        if (potentialFirstDayOfWeek.isAfter(requiredAfter)) {
            return Optional.of(potentialFirstDayOfWeek);
        } else {
            return Optional.empty();
        }
    }

}
