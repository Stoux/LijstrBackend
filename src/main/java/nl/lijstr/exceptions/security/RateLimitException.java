package nl.lijstr.exceptions.security;

import lombok.*;
import nl.lijstr.exceptions.LijstrException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Stoux on 20/04/2016.
 */
@Getter
@ResponseStatus(HttpStatus.FORBIDDEN)
public class RateLimitException extends LijstrException {

    private Long minutesTillNextAttempt;

    /**
     * Create a new {@link RateLimitException}.
     *
     * @param minutesTillNextAttempt Number of minutes before a new attempt is allowed
     */
    public RateLimitException(Long minutesTillNextAttempt) {
        super("Too many attempts");
        this.minutesTillNextAttempt = minutesTillNextAttempt;
    }

    /**
     * Create a new {@link RateLimitException}.
     * No minutesTillNextAttempt is known.
     */
    public RateLimitException() {
        this(null);
    }
}
