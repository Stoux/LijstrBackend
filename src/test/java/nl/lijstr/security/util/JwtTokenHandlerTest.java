package nl.lijstr.security.util;

import nl.lijstr.exceptions.security.AccessExpiredException;
import nl.lijstr.exceptions.security.TokenExpiredException;
import nl.lijstr.security.model.AuthenticationToken;
import nl.lijstr.security.model.JwtGrantedAuthority;
import nl.lijstr.security.model.JwtUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static nl.lijstr._TestUtils.TestUtils.insertMocks;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;


/**
 * Created by Stoux on 20/04/2016.
 */
@ExtendWith(MockitoExtension.class)
public class JwtTokenHandlerTest {

    private JwtTokenHandler jwtTokenHandler;

    @Mock
    private UserDetailsService userDetailsService;

    @BeforeEach
    public void setUp() throws Exception {
        jwtTokenHandler = new JwtTokenHandler();

        String secret = "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
        ReflectionTestUtils.setField(jwtTokenHandler, "secret", secret);
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

    @Test()
    public void parseExpiredToken() throws Exception {
        //Arrange
        JwtUser user = new JwtUser(1L, "A", "B", new ArrayList<>(), null, LocalDateTime.now().minusMinutes(5), 0);
        String token = ReflectionTestUtils.invokeMethod(jwtTokenHandler, "generateToken", user);

        //Act
        assertThrows(
                TokenExpiredException.class,
                () -> jwtTokenHandler.parseToken(token),
                "Token has expired"
        );
    }

    @Test()
    public void parseExpiredAccessToken() throws Exception {
        //Arrange
        JwtUser user = new JwtUser(1L, "A", "B", new ArrayList<>(), LocalDateTime.now().minusMinutes(5), LocalDateTime.now().plusDays(1), 0);
        String token = ReflectionTestUtils.invokeMethod(jwtTokenHandler, "generateToken", user);

        //Act
        assertThrows(
                AccessExpiredException.class,
                () -> jwtTokenHandler.parseToken(token),
                "Access has expired"
        );
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


    @Test()
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
        assertThrows(
                TokenExpiredException.class,
                () -> jwtTokenHandler.refreshToken(token, s -> assertEquals(user.getUsername(), s)),
                "Token has expired"
        );
    }

}