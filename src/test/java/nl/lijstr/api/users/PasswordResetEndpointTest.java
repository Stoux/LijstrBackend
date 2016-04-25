package nl.lijstr.api.users;

import javax.servlet.http.HttpServletRequest;
import nl.lijstr.api.users.models.NewPasswordRequest;
import nl.lijstr.api.users.models.ResetPasswordRequest;
import nl.lijstr.beans.PasswordBean;
import nl.lijstr.beans.UserBean;
import nl.lijstr.domain.users.PasswordReset;
import nl.lijstr.domain.users.User;
import nl.lijstr.services.mail.MailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static nl.lijstr._TestUtils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 25/04/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class PasswordResetEndpointTest {

    @Mock
    private UserBean userBean;
    @Mock
    private PasswordBean passwordBean;
    @Mock
    private MailService mailService;

    private PasswordResetEndpoint endpoint;

    @Before
    public void setUp() throws Exception {
        endpoint = new PasswordResetEndpoint();
        insertMocks(endpoint, userBean, passwordBean, mailService);
    }

    @Test
    public void requestPasswordReset() throws Exception {
        //Arrange
        ResetPasswordRequest request = new ResetPasswordRequest("Username", "Email");
        User user = new User(1L);
        PasswordReset reset = new PasswordReset();
        reset.setUser(user);
        reset.setResetToken("Token");
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);

        when(passwordBean.requestPasswordReset(eq(request.getUsername()), eq(request.getEmail()), eq(servletRequest)))
                .thenReturn(reset);

        //Act
        endpoint.requestPasswordReset(request, servletRequest);

        //Assert
        verify(mailService, times(1)).sendMail(eq("Password reset"), eq(user), any(), eq("password-reset"));
    }

    @Test
    public void resetPassword() throws Exception {
        //Arrange
        NewPasswordRequest request = new NewPasswordRequest("Token", "NewPassword");

        //Act
        endpoint.resetPassword(request);

        //Assert
        verify(passwordBean, times(1)).resetPassword(eq(request.getResetToken()), eq(request.getNewPassword()));
    }

}
