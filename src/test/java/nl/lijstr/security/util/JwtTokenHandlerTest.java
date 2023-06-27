package nl.lijstr.security.util;

import nl.lijstr.exceptions.security.AccessExpiredException;
import nl.lijstr.exceptions.security.TokenExpiredException;
import nl.lijstr.security.model.AuthenticationToken;
import nl.lijstr.security.model.JwtGrantedAuthority;
import nl.lijstr.security.model.JwtUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static nl.lijstr._TestUtils.TestUtils.insertMocks;
import static org.junit.Assert.*;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;


/**
 * Created by Stoux on 20/04/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class JwtTokenHandlerTest {

    private JwtTokenHandler jwtTokenHandler;

    @Mock
    private UserDetailsService userDetailsService;

    @Before
    public void setUp() throws Exception {
        jwtTokenHandler = new JwtTokenHandler();
        ReflectionTestUtils.setField(jwtTokenHandler, "secret", "SECRET");
        insertMocks(jwtTokenHandler, userDetailsService);
    }

    @Test
    public void generateToken() throws Exception {
        //Arrange
        JwtUser user = new JwtUser(1L, "A", "B", new ArrayList<>(), null, null, 1);

        //Act
        AuthenticationToken token = jwtTokenHandler.generateToken(user, false);

        //Assert
        assertEquals(user.getId(), token.getUserId());
        assertEquals(token.getAccessTill(), token.getValidTill());
        assertNotNull(token.getToken());
        assertTrue(LocalDateTime.now().isBefore(token.getAccessTill()));
    }

    @Test
    public void generateRememberMeToken() throws Exception {
        //Arrange
        JwtUser user = new JwtUser(1L, "A", "B", new ArrayList<>(), null, null, 1);

        //Act
        AuthenticationToken token = jwtTokenHandler.generateToken(user, true);

        //Assert
        assertTrue(token.getValidTill().isAfter(token.getAccessTill()));
    }

    @Test
    public void parseGeneratedToken() throws Exception {
        //Arrange
        JwtUser user = new JwtUser(
                1L, "A", "B",
                Arrays.asList(new JwtGrantedAuthority("A"), new JwtGrantedAuthority("B")),
                null, null, 1
        );
        AuthenticationToken token = jwtTokenHandler.generateToken(user, true);

        //Act
        JwtUser foundUser = jwtTokenHandler.parseToken(token.getToken());

        //Assert
        assertEquals(user.getId(), foundUser.getId());
        assertEquals(token.getAccessTill(), foundUser.getAccessTill());
        assertEquals(token.getValidTill(), foundUser.getValidTill());
        assertEquals(user.getUsername(), foundUser.getUsername());
        assertEquals(2, foundUser.getAuthorities().size());
        assertEquals("A", foundUser.getAuthorities().get(0).getPermission());
        assertNull(foundUser.getPassword());
    }

    @Test(expected = TokenExpiredException.class)
    public void parseExpiredToken() throws Exception {
        //Arrange
        JwtUser user = new JwtUser(1L, "A", "B", new ArrayList<>(), null, LocalDateTime.now().minusMinutes(5), 0);
        String token = ReflectionTestUtils.invokeMethod(jwtTokenHandler, "generateToken", user);

        //Act
        jwtTokenHandler.parseToken(token);

        //Assert
        fail("Token has expired");
    }

    @Test(expected = AccessExpiredException.class)
    public void parseExpiredAccessToken() throws Exception {
        //Arrange
        JwtUser user = new JwtUser(1L, "A", "B", new ArrayList<>(), LocalDateTime.now().minusMinutes(5), LocalDateTime.now().plusDays(1), 0);
        String token = ReflectionTestUtils.invokeMethod(jwtTokenHandler, "generateToken", user);

        //Act
        jwtTokenHandler.parseToken(token);

        //Assert
        fail("Access has expired");
    }

    @Test
    public void refreshToken() throws Exception {
        //Arrange
        JwtUser user = new JwtUser(1L, "A", "B", new ArrayList<>(), null, null, 0);

        //Act
        JwtUser foundUser = arrangeRefresh(user, false);

        //Assert
        assertEquals(user.getId(), foundUser.getId());
        assertEquals(foundUser.getValidTill(), foundUser.getAccessTill());
    }

    @Test
    public void refreshRememberMeToken() throws Exception {
        //Arrange
        JwtUser user = new JwtUser(1L, "A", "B", new ArrayList<>(), null, null, 0);

        //Act
        JwtUser foundUser = arrangeRefresh(user, true);

        //Assert
        assertEquals(user.getId(), foundUser.getId());
        assertNotEquals(foundUser.getValidTill(), foundUser.getAccessTill());
    }

    private JwtUser arrangeRefresh(JwtUser user, boolean rememberMe) {
        //Arrange
        AuthenticationToken token = jwtTokenHandler.generateToken(user, rememberMe);
        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(user);

        //Act
        AuthenticationToken authenticationToken = jwtTokenHandler.refreshToken(
                token.getToken(), username -> assertEquals(user.getUsername(), username)
        );
        return jwtTokenHandler.parseToken(authenticationToken.getToken());
    }


    @Test(expected = TokenExpiredException.class)
    public void refreshExpiredToken() throws Exception {
        //Arrange
        JwtUser user = new JwtUser(
                1L, "A", "B", new ArrayList<>(),
                LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now().plusMinutes(5),
                0
        );
        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(user);
        String token = ReflectionTestUtils.invokeMethod(jwtTokenHandler, "generateToken", user);
        user.setValidatingKey(1);

        //Act
        jwtTokenHandler.refreshToken(token, s -> assertEquals(user.getUsername(), s));

        //Assert
        fail("Token has expired");
    }

}