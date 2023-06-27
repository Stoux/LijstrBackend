package nl.lijstr.beans;

import nl.lijstr.domain.users.PasswordReset;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.BadRequestException;
import nl.lijstr.exceptions.db.ConflictException;
import nl.lijstr.exceptions.db.NotFoundException;
import nl.lijstr.repositories.users.PasswordResetRepository;
import nl.lijstr.repositories.users.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.time.LocalDateTime;

import static nl.lijstr._TestUtils.TestUtils.getInvocationParam;
import static nl.lijstr._TestUtils.TestUtils.insertMocks;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 23/04/2016.
 */
@ExtendWith(MockitoExtension.class)
public class PasswordBeanTest {

    private static SecureRandom secureRandom;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordResetRepository passwordResetRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private PasswordBean passwordBean;

    @BeforeAll
    public static void createSecureRandom() {
        secureRandom = new SecureRandom();
    }

    @BeforeEach
    public void setUp() throws Exception {
        passwordBean = new PasswordBean();
        insertMocks(passwordBean, userRepository, passwordResetRepository, passwordEncoder, secureRandom);
    }

    @Test
    public void requestPasswordReset() throws Exception {
        //Arrange
        User user = new User(1);
        when(userRepository.findByUsernameAndEmail(anyString(), anyString()))
                .thenReturn(user);
        when(passwordResetRepository.findFirstByUserOrderByCreatedDesc(any()))
                .thenReturn(null);
        when(passwordResetRepository.save(any(PasswordReset.class)))
                .thenAnswer(invocation -> getInvocationParam(invocation, 0));

        HttpServletRequest request = mock(HttpServletRequest.class);
        String requestRemote = "Remote";
        when(request.getRemoteAddr()).thenReturn(requestRemote);
        int requestPort = 1337;
        when(request.getRemotePort()).thenReturn(requestPort);
        String requestUserAgent = "Test User";
        when(request.getHeader(eq("user-agent"))).thenReturn(requestUserAgent);

        //Act
        PasswordReset reset = passwordBean.requestPasswordReset("A", "B", request);

        //Assert
        assertEquals(user, reset.getUser());
        assertEquals(requestRemote, reset.getRemoteAddress());
        assertEquals(Integer.valueOf(requestPort), reset.getUsedPort());
        assertEquals(requestUserAgent, reset.getUserAgent());
        assertFalse(reset.isUsed());
        assertFalse(reset.getResetToken().isEmpty());
    }

    @Test()
    public void passwordResetNonExistentUser() throws Exception {
        //Arrange
        when(userRepository.findByUsernameAndEmail(anyString(), anyString()))
                .thenReturn(null);

        //Act
        assertThrows(
                NotFoundException.class,
                () -> passwordBean.requestPasswordReset("A", "B", null),
                "User shouldn't have been found"
        );
    }

    @Test()
    public void conflictPasswordReset() throws Exception {
        //Arrange
        when(userRepository.findByUsernameAndEmail(anyString(), anyString()))
                .thenReturn(new User(1));
        PasswordReset passwordReset = new PasswordReset(
                "", 0, "", "", LocalDateTime.now().plusMinutes(1), false
        );
        when(passwordResetRepository.findFirstByUserOrderByCreatedDesc(any()))
                .thenReturn(passwordReset);

        //Act
        assertThrows(
                ConflictException.class,
                () ->  passwordBean.requestPasswordReset("A", "B", null),
                "Already an open passwordReset!"
        );
    }

    @Test
    public void resetPassword() throws Exception {
        //Arrange
        String newPassword = "NewPassword";
        User user = new User(1);
        PasswordReset passwordReset = new PasswordReset("", 0, "", "", LocalDateTime.now().plusMinutes(1), false);
        passwordReset.setUser(user);

        when(passwordResetRepository.findByResetToken(anyString()))
                .thenReturn(passwordReset);
        when(passwordEncoder.encode(anyString()))
                .thenAnswer(invocation -> "$" + getInvocationParam(invocation, 0));

        //Act
        passwordBean.resetPassword("Token", newPassword);

        //Assert
        assertTrue(passwordReset.isUsed());
        verify(passwordResetRepository, times(1)).save(eq(passwordReset));

        assertEquals("$" + newPassword, user.getHashedPassword());
        assertEquals(1, user.getValidatingKey());
        verify(userRepository, times(1)).save(eq(user));
    }

    @Test()
    public void nonExistentPasswordReset() throws Exception {
        //Arrange
        when(passwordResetRepository.findByResetToken(anyString()))
                .thenReturn(null);

        //Act
        assertThrows(
                NotFoundException.class,
                () -> passwordBean.resetPassword("nonExistentToken", "password"),
                "PasswordReset doesn't exist"
        );
    }

    @Test()
    public void usedPasswordReset() {
        //Arrange
        when(passwordResetRepository.findByResetToken(anyString()))
                .thenReturn(new PasswordReset("", 0, "", "", LocalDateTime.MAX, true));

        //Act
        assertThrows(
                BadRequestException.class,
                () -> passwordBean.resetPassword("usedToken", "password"),
                "PasswordReset is already used"
        );
    }

    @Test()
    public void expiredPasswordReset() {
        //Arrange
        when(passwordResetRepository.findByResetToken(anyString()))
                .thenReturn(new PasswordReset("", 0, "", "", LocalDateTime.MIN, false));

        //Act
        assertThrows(
                BadRequestException.class,
                () -> passwordBean.resetPassword("expiredToken", "password"),
                "PasswordReset is expired"
        );
    }

}