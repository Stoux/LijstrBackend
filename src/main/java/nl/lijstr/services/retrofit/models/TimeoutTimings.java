package nl.lijstr.services.retrofit.models;

import lombok.*;

/**
 * Timings for HTTP clients.
 */
@Getter
@AllArgsConstructor
public class TimeoutTimings {

    private final int connect;
    private final int read;
    private final int write;

}
