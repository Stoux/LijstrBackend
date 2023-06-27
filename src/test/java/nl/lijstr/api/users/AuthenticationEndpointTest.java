package nl.lijstr.api.users;

import nl.lijstr.api.users.models.AuthenticationRequest;
import nl.lijstr.api.users.models.RefreshRequest;
import nl.lijstr.common.Container;
import nl.lijstr.domain.users.LoginAttempt;
import nl.lijstr.exceptions.security.AccessExpiredException;
import nl.lijstr.exceptions.security.RateLimitException;
import nl.lijstr.repositories.users.LoginAttemptRepository;
import nl.lijstr.security.model.AuthenticationToken;
import nl.lijstr.security.model.JwtUser;
import nl.lijstr.security.util.JwtTokenHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static nl.lijstr._TestUtils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 25/04/2016.
 */
@ExtendWith(MockitoExtension.class)
public class AuthenticationEndpointTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private LoginAttemptRepository loginAttemptRepository;
    @Mock
    private JwtTokenHandler jwtTokenHandler;

    private AuthenticationEndpoint endpoint;

    @BeforeEach
    public void setUp() throws Exception {
        endpoint = new AuthenticationEndpoint();
        insertMocks(endpoint, authenticationManager, userDetailsService, loginAttemptRepository, jwtTokenHandler);
    }

    @Test
    public void authenticate() throws Exception {
        //Arrange
        String username = "Username";
        boolean rememberMe = true;

        JwtUser user = createUser(1L);
        HttpServletRequest servletRequest = createServletRequest();
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, "Password", rememberMe);
        LoginAttempt.Type type = LoginAttempt.Type.AUTHENTICATION;
        AuthenticationToken token =
                new AuthenticationToken("Token", LocalDateTime.now(), LocalDateTime.now(), user.getId());

        Container<LoginAttempt> attemptContainer = new Container<>();
        when(loginAttemptRepository.findByRemoteAddressAndSuccessAndLoginTypeAndTimestampAfterOrderByTimestampAsc(
                eq(servletRequest.getRemoteAddr()), eq(false), eq(type), any()
        )).thenReturn(new ArrayList<>());
        when(userDetailsService.loadUserByUsername(eq(username))).thenReturn(user);
        when(jwtTokenHandler.generateToken(eq(user), eq(rememberMe)))
                .thenReturn(token);
        when(loginAttemptRepository.saveAndFlush(any())).thenAnswer(invocation -> {
            attemptContainer.setItem(getInvocationParam(invocation, 0));
            return attemptContainer.getItem();
        });

        //Act
        AuthenticationToken authenticationToken = endpoint.authenticate(authenticationRequest, servletRequest);

        //Assert
        assertEquals(token, authenticationToken);
        assertTrue(attemptContainer.isPresent());
        LoginAttempt loginAttempt = attemptContainer.getItem();
        assertEquals(user.getId(), loginAttempt.getUser().getId());
        assertEquals(servletRequest.getRemoteAddr(), loginAttempt.getRemoteAddress());
        assertEquals(Integer.valueOf(servletRequest.getRemotePort()), loginAttempt.getUsedPort());
        assertEquals(servletRequest.getHeader("user-agent"), loginAttempt.getUserAgent());
        assertEquals(type, loginAttempt.getLoginType());
        assertNull(loginAttempt.getRejectionReason());
        assertEquals(username, loginAttempt.getUsername());
    }

    @Test
    public void authenticateWrongPassword() throws Exception {
        //Arrange
        HttpServletRequest servletRequest = createServletRequest();
        Container<UsernamePasswordAuthenticationToken> tokenContainer = new Container<>();
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("Username", "Password", true);

        findLoginAttempts(new ArrayList<>());
        when(authenticationManager.authenticate(any())).thenAnswer(invocation -> {
            tokenContainer.setItem(getInvocationParam(invocation, 0));
            throw new BadCredentialsException("Test");
        });

        //Act
        try {
            endpoint.authenticate(authenticationRequest, servletRequest);
            fail("Wrong credentials");
        } catch (BadCredentialsException e) {
            assertEquals("Test", e.getMessage());
        }

        //Assert
        assertTrue(tokenContainer.isPresent());
        UsernamePasswordAuthenticationToken authToken = tokenContainer.getItem();
        assertEquals(authenticationRequest.getUsername(), authToken.getPrincipal());
        assertEquals(authenticationRequest.getPassword(), authToken.getCredentials());
    }

    @Test
    public void authenticateTooManyAttempts() throws Exception {
        //Arrange
        HttpServletRequest servletRequest = createServletRequest();
        Container<LoginAttempt> attemptContainer = new Container<>();

        LoginAttempt oldestLogin = new LoginAttempt();
        oldestLogin.setTimestamp(LocalDateTime.now());
        List<LoginAttempt> loginAttempts = Arrays.asList(
                oldestLogin, oldestLogin, oldestLogin, oldestLogin, oldestLogin
        );
        findLoginAttempts(loginAttempts);
        when(loginAttemptRepository.saveAndFlush(any())).thenAnswer(invocation -> {
            attemptContainer.setItem(getInvocationParam(invocation, 0));
            return attemptContainer.getItem();
        });

        //Act
        try {
            endpoint.authenticate(new AuthenticationRequest("Username", "Password", true), servletRequest);
            fail("Rate limit should be reached");
        } catch (RateLimitException e) {
            assertTrue(e.getMinutesTillNextAttempt() < 31 && e.getMinutesTillNextAttempt() > 28);
        }

        //Assert
        assertTrue(attemptContainer.isPresent());
        LoginAttempt loginAttempt = attemptContainer.getItem();
        assertFalse(loginAttempt.isSuccess());
        assertNotNull(loginAttempt.getRejectionReason());
    }

    @Test
    public void refreshToken() throws Exception {
        //Arrange
        String currentToken = "currentToken";
        RefreshRequest refreshRequest = new RefreshRequest(currentToken);
        HttpServletRequest servletRequest = createServletRequest();
        Container<LoginAttempt> attemptContainer = new Container<>();
        AuthenticationToken newToken = new AuthenticationToken(
                currentToken + "2", LocalDateTime.now(), LocalDateTime.now(), 1L
        );

        findLoginAttempts(new ArrayList<>());
        when(jwtTokenHandler.refreshToken(eq(currentToken), any())).thenReturn(newToken);
        when(loginAttemptRepository.saveAndFlush(any())).thenAnswer(invocation -> {
            attemptContainer.setItem(getInvocationParam(invocation, 0));
            return attemptContainer.getItem();
        });

        //Act
        AuthenticationToken refreshedToken = endpoint.refreshToken(refreshRequest, servletRequest);

        //Assert
        assertTrue(attemptContainer.isPresent());
        LoginAttempt loginAttempt = attemptContainer.getItem();
        verify(loginAttemptRepository, times(1)).saveAndFlush(eq(loginAttempt));
        assertTrue(loginAttempt.isSuccess());
        assertEquals(newToken, refreshedToken);
    }

    @Test
    public void refreshInvalidToken() throws Exception {
        //Arrange
        HttpServletRequest servletRequest = createServletRequest();
        Container<LoginAttempt> attemptContainer = new Container<>();

        findLoginAttempts(new ArrayList<>());
        AccessExpiredException accessExpiredException = new AccessExpiredException();
        when(jwtTokenHandler.refreshToken(anyString(), any())).thenThrow(accessExpiredException);
        when(loginAttemptRepository.saveAndFlush(any())).thenAnswer(invocation -> {
            attemptContainer.setItem(getInvocationParam(invocation, 0));
            return attemptContainer.getItem();
        });

        //Act
        try {
            endpoint.refreshToken(new RefreshRequest("currentToken"), servletRequest);
            fail("Token shouldn't be refreshed");
        } catch (AccessExpiredException e) {
            assertEquals(accessExpiredException, e);
        }

        //Assert
        assertTrue(attemptContainer.isPresent());
        verify(loginAttemptRepository, times(1)).saveAndFlush(any());
        LoginAttempt loginAttempt = attemptContainer.getItem();
        assertNotNull(loginAttempt.getRejectionReason());
        assertFalse(loginAttempt.isSuccess());
    }


    private HttpServletRequest createServletRequest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("user-agent")).thenReturn("UserAgent");
        when(request.getRemoteAddr()).thenReturn("RemoteAddr");
        when(request.getRemotePort()).thenReturn(1337);
        return request;
    }

    private void findLoginAttempts(List<LoginAttempt> list) {
        when(loginAttemptRepository.findByRemoteAddressAndSuccessAndLoginTypeAndTimestampAfterOrderByTimestampAsc(
                anyString(), anyBoolean(), any(), any()
        )).thenReturn(list);
    }

}