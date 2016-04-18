package nl.lijstr.security.util;

import com.google.gson.Gson;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import nl.lijstr.exceptions.security.AccessExpiredException;
import nl.lijstr.security.model.JwtUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -3301605591108950415L;

    private static final long ACCESS_MINUTES = 30;
    private static final long REMEMBER_ME_MINUTES = 60 * 24 * 30;

    @Value("${jwt.secret}")
    private String secret;

    private Gson gsonInstance = new Gson();

    /**
     * Parse a token into a validated JwtUser.
     * This method throws {@link RuntimeException}s if the authentication failed.
     *
     * @param token The token
     *
     * @return the user
     */
    public JwtUser parseToken(String token) {
        //Parse the token & convert it
        String userJson = Jwts.parser()
                .setSigningKey(secret)
                .parsePlaintextJws(token)
                .getBody();
        JwtUser user = gsonInstance.fromJson(userJson, JwtUser.class);

        //Validate it
        if (user.getAccessTill().isBefore(LocalDateTime.now())) {
            throw new AccessExpiredException();
        }

        return user;
    }

    /**
     * Generate a token based on a JwtUser.
     *
     * @param user       The user
     * @param rememberMe Should be active for a rememberMe length
     *
     * @return the token
     */
    public String generateToken(JwtUser user, boolean rememberMe) {
        user.setAccessTill(LocalDateTime.now().plusMinutes(ACCESS_MINUTES));
        return Jwts.builder()
                .setPayload(gsonInstance.toJson(user))
                .setExpiration(nowPlusMinutes(rememberMe ? REMEMBER_ME_MINUTES : ACCESS_MINUTES))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    private static Date nowPlusMinutes(long minutes) {
        LocalDateTime time = LocalDateTime.now().plusMinutes(minutes);
        Instant instant = time.toInstant(ZoneOffset.of(ZoneOffset.systemDefault().getId()));
        return Date.from(instant);
    }

}