package nl.lijstr.security.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import nl.lijstr.exceptions.security.AccessExpiredException;
import nl.lijstr.exceptions.security.TokenExpiredException;
import nl.lijstr.security.model.AuthenticationToken;
import nl.lijstr.security.model.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * A JSON Web Tokens Spring component that has various methods that ease the JWT flow.
 */
@Component
public class JwtTokenHandler {

    private static final long ACCESS_MINUTES = 30L;
    private static final long REMEMBER_ME_MINUTES = 60L * 24L * 30L;

    private static final String JSON_PREFIX = "-";
    private static final int JSON_PREFIX_LENGTH = JSON_PREFIX.length();
    private final Gson gsonInstance;
    @Value("${jwt.secret}")
    private String secret;
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Create a new {@link JwtTokenHandler}.
     */
    public JwtTokenHandler() {
        gsonInstance = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .create();
    }

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
        return parseTokenIntoUser(token, true, null);
    }

    private JwtUser parseTokenIntoUser(String token, boolean checkAccess, Consumer<String> usernameCallback) {
        Jws<String> jws = Jwts.parser()
                .setSigningKey(secret)
                .parsePlaintextJws(token);

        JwtUser user = gsonInstance.fromJson(jws.getBody().substring(JSON_PREFIX_LENGTH), JwtUser.class);
        if (usernameCallback != null) {
            usernameCallback.accept(user.getUsername());
        }

        //Check if expired
        if (user.getValidTill().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException();
        }

        //Check if still access
        if (checkAccess && user.getAccessTill().isBefore(LocalDateTime.now())) {
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
    public AuthenticationToken generateToken(JwtUser user, boolean rememberMe) {
        user.setAccessTill(LocalDateTime.now().plusMinutes(ACCESS_MINUTES));
        user.setValidTill(rememberMe ? LocalDateTime.now().plusMinutes(REMEMBER_ME_MINUTES) : user.getAccessTill());
        String token = generateToken(user);
        return new AuthenticationToken(token, user.getAccessTill(), user.getValidTill(), user.getId());
    }

    private String generateToken(JwtUser user) {
        return Jwts.builder()
                .setPayload(JSON_PREFIX + gsonInstance.toJson(user))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * Refresh a token.
     *
     * @param token            The token
     * @param usernameCallback A callback that can be called if a valid username is detected
     *
     * @return The new token
     */
    public AuthenticationToken refreshToken(String token, Consumer<String> usernameCallback) {
        //Validate the token
        JwtUser user = parseTokenIntoUser(token, false, usernameCallback);

        //Refresh the user (updates permissions etc)
        JwtUser refreshedUser = (JwtUser) userDetailsService.loadUserByUsername(user.getUsername());

        //Check the validating key
        if (user.getValidatingKey() != refreshedUser.getValidatingKey()) {
            throw new TokenExpiredException();
        }

        boolean rememberMe = !(user.getAccessTill().isEqual(user.getValidTill()));
        return generateToken(refreshedUser, rememberMe);
    }

}
