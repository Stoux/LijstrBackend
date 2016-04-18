package nl.lijstr.security.model;

import java.time.LocalDateTime;
import lombok.*;

/**
 * Created by Stoux on 18/04/2016.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationToken {

    private String token;
    private LocalDateTime accessTill;
    private LocalDateTime validTill;

}
