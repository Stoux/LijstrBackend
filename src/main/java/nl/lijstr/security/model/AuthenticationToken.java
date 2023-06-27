package nl.lijstr.security.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private Long userId;

}
