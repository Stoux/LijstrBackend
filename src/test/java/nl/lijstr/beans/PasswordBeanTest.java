package nl.lijstr.beans;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import javax.servlet.http.HttpServletRequest;
import nl.lijstr.domain.users.PasswordReset;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.BadRequestException;
import nl.lijstr.exceptions.db.ConflictException;
import nl.lijstr.exceptions.db.NotFoundException;
import nl.lijstr.repositories.users.PasswordResetRepository;
import nl.lijstr.repositories.users.UserRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import static nl.lijstr._TestUtils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 23/04/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class PasswordBeanTest {

    private static SecureRandom secureRandom;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordResetRepository passwordResetRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private PasswordBean passwordBean;

    @BeforeClass
    public static void createSecureRandom() {
        secureRandom = new SecureRandom();
    }

    @Before
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

    @Test(expected = NotFoundException.class)
    public void passwordResetNonExistentUser() throws Exception {
        //Arrange
        when(userRepository.findByUsernameAndEmail(anyString(), anyString()))
                .thenReturn(null);

        //Act
        passwordBean.requestPasswordReset("A", "B", null);

        //Assert
        fail("User shouldn't been found.");
    }

    @Test(expected = ConflictException.class)
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
        passwordBean.requestPasswordReset("A", "B", null);

        //Assert
        fail("Already an open passwordReset");
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

    @Test(expected = NotFoundException.class)
    public void nonExistentPasswordReset() throws Exception {
        //Arrange
        when(passwordResetRepository.findByResetToken(anyString()))
                .thenReturn(null);

        //Act
        passwordBean.resetPassword("nonExistentToken", "password");

        //Assert
        fail("PasswordReset doesn't exist");
    }

    @Test(expected = BadRequestException.class)
    public void usedPasswordReset() {
        //Arrange
        when(passwordResetRepository.findByResetToken(anyString()))
                .thenReturn(new PasswordReset("", 0, "", "", LocalDateTime.MAX, true));

        //Act
        passwordBean.resetPassword("usedToken", "password");

        //Assert
        fail("PasswordReset is already used");
    }

    @Test(expected = BadRequestException.class)
    public void expiredPasswordReset() {
        //Arrange
        when(passwordResetRepository.findByResetToken(anyString()))
                .thenReturn(new PasswordReset("", 0, "", "", LocalDateTime.MIN, false));

        //Act
        passwordBean.resetPassword("expiredToken", "password");

        //Assert
        fail("PasswordReset is expired");
    }

}