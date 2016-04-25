package nl.lijstr.api.users;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.lijstr.api.users.models.CreateUserRequest;
import nl.lijstr.api.users.models.PasswordChangeRequest;
import nl.lijstr.api.users.models.PermissionList;
import nl.lijstr.beans.UserBean;
import nl.lijstr.domain.other.ApprovedFor;
import nl.lijstr.domain.users.GrantedPermission;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.BadRequestException;
import nl.lijstr.exceptions.security.UnauthorizedException;
import nl.lijstr.repositories.users.PermissionRepository;
import nl.lijstr.repositories.users.UserRepository;
import nl.lijstr.security.model.JwtGrantedAuthority;
import nl.lijstr.security.model.JwtUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static nl.lijstr._TestUtils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 25/04/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserEndpointTest {

    @Mock
    private UserBean userBean;
    @Mock
    private PermissionRepository permissionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UserEndpoint endpoint;

    @Before
    public void setUp() throws Exception {
        endpoint = new UserEndpoint();
        insertMocks(endpoint, userBean, permissionRepository, userRepository, passwordEncoder);
    }

    @Test
    public void getUserDetails() throws Exception {
        //Arrange
        long userId = 1L;
        JwtUser jwtUser = createUser(userId);
        User user = new User(userId);

        when(userBean.getJwtUser()).thenReturn(jwtUser);
        when(userRepository.findOne(eq(userId))).thenReturn(user);

        //Act
        User foundUser = endpoint.getUserDetails(userId);

        //Arrange
        assertEquals(user, foundUser);
    }

    @Test(expected = BadRequestException.class)
    public void getInvalidUserDetails() throws Exception {
        //Act
        endpoint.getUserDetails(null);

        //Fail
        fail("User ID is invalid");
    }


    @Test(expected = UnauthorizedException.class)
    public void getOtherUserDetails() throws Exception {
        //Arrange
        JwtUser jwtUser = createUser(1L);
        ReflectionTestUtils.setField(jwtUser, "authorities", new ArrayList<>());
        when(userBean.getJwtUser()).thenReturn(jwtUser);

        //Act
        endpoint.getUserDetails(2L);

        //Fail
        fail("User has no admin permission");
    }

    @Test
    public void getOtherUserDetailsAsAdmin() throws Exception {
        //Arrange
        JwtUser jwtUser = createUser(1L);
        List<JwtGrantedAuthority> permissions =
                Arrays.asList(new JwtGrantedAuthority(Permission.USER), new JwtGrantedAuthority(Permission.ADMIN));
        ReflectionTestUtils.setField(jwtUser, "authorities", permissions);

        User otherUser = new User(2L);

        when(userBean.getJwtUser()).thenReturn(jwtUser);
        when(userRepository.findOne(eq(otherUser.getId()))).thenReturn(otherUser);

        //Act
        User foundUser = endpoint.getUserDetails(otherUser.getId());

        //Assert
        assertEquals(otherUser, foundUser);
    }

    @Test
    public void getPermissions() throws Exception {
        //Arrange
        JwtUser jwtUser = createUser(1L);
        User user = new User(1L);
        GrantedPermission grantedPermission = new GrantedPermission();
        List<GrantedPermission> permissions = Arrays.asList(grantedPermission);
        user.setGrantedPermissions(permissions);

        when(userBean.getJwtUser()).thenReturn(jwtUser);
        when(userRepository.findOne(eq(jwtUser.getId()))).thenReturn(user);

        //Act
        List<GrantedPermission> foundPermissions = endpoint.getPermissions(jwtUser.getId());

        //Assert
        assertEquals(1, foundPermissions.size());
        assertEquals(grantedPermission, foundPermissions.get(0));
        assertEquals(permissions, foundPermissions);
    }

    @Test
    public void modifyPermissions() throws Exception {
        //Arrange
        User user = new User(1L);
        List<GrantedPermission> currentPermissions = new ArrayList<>();
        currentPermissions.add(new GrantedPermission(new Permission(Permission.USER)));
        currentPermissions.add(new GrantedPermission(new Permission(Permission.ADMIN)));
        user.setGrantedPermissions(currentPermissions);

        Permission movieUserPermission = new Permission(Permission.MOVIE_USER);
        PermissionList permissionList = new PermissionList(Arrays.asList(Permission.USER, Permission.MOVIE_USER));

        when(userRepository.findOne(eq(user.getId()))).thenReturn(user);
        when(permissionRepository.findByName(eq(Permission.MOVIE_USER))).thenReturn(movieUserPermission);

        //Act
        List<GrantedPermission> newPermissions = endpoint.modifyPermissions(user.getId(), permissionList);

        //Arrange
        assertEquals(currentPermissions, newPermissions);
        assertEquals(2, newPermissions.size());
        assertEquals(Permission.USER, newPermissions.get(0).getPermission().getName());
        assertEquals(movieUserPermission, newPermissions.get(1).getPermission());
        verify(userRepository, times(1)).saveAndFlush(any());
    }

    @Test(expected = BadRequestException.class)
    public void modifyPermissionsInvalid() throws Exception {
        //Arrange
        PermissionList invalidList = new PermissionList(Arrays.asList(Permission.USER, "INVALID"));

        //Act
        endpoint.modifyPermissions(1L, invalidList);

        //Fail
        fail("Permission doesn't exist");
    }

    @Test
    public void changePassword() throws Exception {
        //Arrange
        String currentPassword = "currentPassword";
        String newPassword = "newPassword";
        JwtUser jwtUser = createUser(1L);
        User user = new User(1L);
        user.setHashedPassword(currentPassword);
        user.setValidatingKey(1);

        when(userBean.getJwtUser()).thenReturn(jwtUser);
        when(userRepository.findOne(eq(user.getId()))).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenAnswer(invocation -> {
            String s1 = getInvocationParam(invocation, 0);
            String s2 = getInvocationParam(invocation, 1);
            return s1.equals(s2);
        });
        when(passwordEncoder.encode(anyString()))
                .thenAnswer(invocation -> "$" + getInvocationParam(invocation, 0));

        //Act
        endpoint.changePassword(jwtUser.getId(), new PasswordChangeRequest(currentPassword, newPassword));

        //Assert
        assertEquals("$" + newPassword, user.getHashedPassword());
        assertEquals(2, user.getValidatingKey());
        verify(userRepository, times(1)).save(eq(user));
    }

    @Test(expected = UnauthorizedException.class)
    public void otherUserPasswordChange() throws Exception {
        //Arrange
        JwtUser jwtUser = createUser(1L);
        when(userBean.getJwtUser()).thenReturn(jwtUser);

        //Act
        endpoint.changePassword(2L, null);

        //Fail
        fail("Trying to change another user's password");
    }

    @Test(expected = BadRequestException.class)
    public void invalidPasswordChange() throws Exception {
        //Arrange
        JwtUser jwtUser = createUser(1L);
        User user = new User(1L);
        user.setHashedPassword("C");

        when(userBean.getJwtUser()).thenReturn(jwtUser);
        when(userRepository.findOne(eq(user.getId()))).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        //Act
        endpoint.changePassword(jwtUser.getId(), new PasswordChangeRequest("A", "B"));

        //Fail
        fail("Password didn't match");
    }

    @Test
    public void addUser() throws Exception {
        //Arrange
        String username = "TestUser";
        String email = "TestEmail";
        CreateUserRequest createUserRequest = new CreateUserRequest(username, "Display", email, ApprovedFor.HARDCORE);

        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(null);
        when(userRepository.saveAndFlush(any())).thenAnswer(invocation -> getInvocationParam(invocation, 0));

        //Act
        User newUser = endpoint.addUser(createUserRequest);

        //Assert
        assertEquals(username, newUser.getUsername());
        assertEquals(email, newUser.getEmail());
        assertEquals(createUserRequest.getDisplayName(), newUser.getDisplayName());
        assertEquals(createUserRequest.getApprovedFor(), newUser.getApprovedFor());
        verify(userRepository, times(1)).saveAndFlush(eq(newUser));
        verify(userRepository, times(1)).findByUsernameOrEmail(eq(username), eq(email));
    }

    @Test(expected = BadRequestException.class)
    public void addExistingUser() throws Exception {
        //Arrange
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(new User(1L));

        //Act
        endpoint.addUser(new CreateUserRequest("A", "B", "C", null));

        //Fail
        fail("User already exists");
    }

}
