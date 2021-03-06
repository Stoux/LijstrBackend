package nl.lijstr.api.users;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import nl.lijstr.api.abs.AbsService;
import nl.lijstr.api.users.models.AuthenticationRequest;
import nl.lijstr.api.users.models.RefreshRequest;
import nl.lijstr.domain.users.LoginAttempt;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.security.RateLimitException;
import nl.lijstr.repositories.users.LoginAttemptRepository;
import nl.lijstr.security.model.AuthenticationToken;
import nl.lijstr.security.model.JwtUser;
import nl.lijstr.security.util.JwtTokenHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Stoux on 18/04/2016.
 */
@RestController
@RequestMapping(value = "/auth", produces = "application/json")
public class AuthenticationEndpoint extends AbsService {

    /**
     * The maximum amount of attempts that can be made per {@link nl.lijstr.domain.users.LoginAttempt.Type} in
     * the last X minutes (see MAX_ATTEMPTS_MINUTES).
     */
    public static final long MAX_ATTEMPTS = 5L;
    /**
     * The number of minutes in the past to check for attempts.
     */
    public static final long MAX_ATTEMPTS_MINUTES = 30L;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private LoginAttemptRepository loginAttemptRepository;

    @Autowired
    private JwtTokenHandler jwtTokenHandler;

    /**
     * Allows an user to authenticate and receive an access token.
     *
     * @param authenticationRequest The authentication request
     * @param servletRequest        The spring request
     *
     * @return the token
     */
    @SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.coding.IllegalCatchCheck")
    @RequestMapping(method = RequestMethod.POST)
    public AuthenticationToken authenticate(@Valid @RequestBody AuthenticationRequest authenticationRequest,
                                            HttpServletRequest servletRequest) {
        LoginAttempt loginAttempt = createLoginAttempt(
                servletRequest, authenticationRequest.getUsername(), LoginAttempt.Type.AUTHENTICATION
        );

        try {
            //Try to login
            AuthenticationToken token = authenticate(authenticationRequest);
            loginSuccess(loginAttempt, token);
            return token;
        } catch (RuntimeException e) {
            loginFail(loginAttempt, e);
            throw e;
        }
    }

    private AuthenticationToken authenticate(AuthenticationRequest authenticationRequest) {
        //Authenticate
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );

        //Generate the token
        final JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        return jwtTokenHandler.generateToken(jwtUser, authenticationRequest.isRememberMe());
    }

    /**
     * Refresh an existing token (if it's still valid).
     *
     * @param refreshRequest The current token
     * @param servletRequest The spring request
     *
     * @return the new token
     */
    @SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.coding.IllegalCatchCheck")
    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public AuthenticationToken refreshToken(@Valid @RequestBody RefreshRequest refreshRequest,
                                            HttpServletRequest servletRequest) {
        LoginAttempt loginAttempt = createLoginAttempt(
                servletRequest, null, LoginAttempt.Type.TOKEN_REFRESH
        );

        try {
            AuthenticationToken token = jwtTokenHandler.refreshToken(
                    refreshRequest.getCurrentToken(), loginAttempt::setUsername
            );
            loginSuccess(loginAttempt, token);
            return token;
        } catch (RuntimeException e) {
            loginFail(loginAttempt, e);
            throw e;
        }
    }

    private LoginAttempt createLoginAttempt(HttpServletRequest servletRequest, String username,
                                            LoginAttempt.Type loginType) {
        //Create the attempt
        LoginAttempt attempt = new LoginAttempt(
                servletRequest.getRemoteAddr(), servletRequest.getRemotePort(),
                servletRequest.getHeader("user-agent"), username, loginType
        );

        //Check number of attempts in the last X minutes
        List<LoginAttempt> attempts =
                loginAttemptRepository.findByRemoteAddressAndSuccessAndLoginTypeAndTimestampAfterOrderByTimestampAsc(
                        servletRequest.getRemoteAddr(), false, loginType,
                        LocalDateTime.now().minusMinutes(MAX_ATTEMPTS_MINUTES)
                );
        if (attempts.size() >= MAX_ATTEMPTS) {
            attempt.fail("Too many attempts");
            loginAttemptRepository.saveAndFlush(attempt);

            //Check when the next attempt is allowed
            LocalDateTime oldestLogin = attempts.get(0).getTimestamp();
            long minutesTill = oldestLogin.until(LocalDateTime.now(), ChronoUnit.MINUTES);
            throw new RateLimitException(MAX_ATTEMPTS_MINUTES - minutesTill);
        }

        return attempt;
    }

    private void loginSuccess(LoginAttempt loginAttempt, AuthenticationToken token) {
        loginAttempt.setUser(new User(token.getUserId()));
        loginAttemptRepository.saveAndFlush(loginAttempt);
    }

    private void loginFail(LoginAttempt loginAttempt, RuntimeException e) {
        loginAttempt.fail(e.getMessage());
        loginAttemptRepository.saveAndFlush(loginAttempt);
    }

}
