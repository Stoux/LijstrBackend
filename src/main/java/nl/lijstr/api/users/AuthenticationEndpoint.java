package nl.lijstr.api.users;

import nl.lijstr.api.abs.AbsService;
import nl.lijstr.api.users.models.AuthenticationRequest;
import nl.lijstr.api.users.models.RefreshRequest;
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

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenHandler jwtTokenHandler;

    /**
     * Allows an user to authenticate and receive an access token.
     *
     * @param authenticationRequest The request
     *
     * @return the token
     */
    @RequestMapping(method = RequestMethod.POST)
    public AuthenticationToken authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
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
     *
     * @return the new token
     */
    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public AuthenticationToken refreshToken(@RequestBody RefreshRequest refreshRequest) {
        return jwtTokenHandler.refreshToken(refreshRequest.getCurrentToken());
    }

}
