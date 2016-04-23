package nl.lijstr.security;

import java.util.ArrayList;
import java.util.List;
import nl.lijstr.domain.other.ApprovedFor;
import nl.lijstr.domain.users.GrantedPermission;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.users.UserRepository;
import nl.lijstr.security.model.JwtGrantedAuthority;
import nl.lijstr.security.model.JwtUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import static nl.lijstr._TestUtils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 20/04/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class JwtUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private JwtUserDetailsService userDetailsService;

    @Before
    public void setUp() throws Exception {
        userDetailsService = new JwtUserDetailsService();
        insertMocks(userDetailsService, userRepository);
        when(userRepository.findByUsername(anyString()))
                .thenReturn(null);
    }

    @Test
    public void loadUser() throws Exception {
        //Arrange
        User user = new User("A", "B", "C", ApprovedFor.EVERYONE);
        ReflectionTestUtils.setField(user, "id", 1L);
        List<GrantedPermission> permissions = new ArrayList<>();
        for (String permission : Permission.list()) {
            permissions.add(new GrantedPermission(new Permission(permission)));
        }
        user.setGrantedPermissions(permissions);
        when(userRepository.findByUsername(eq("A")))
                .thenReturn(user);

        //Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("A");
        JwtUser jwtUser = (JwtUser) userDetails;

        //Assert
        assertNotNull(jwtUser);
        assertEquals(user.getId(), jwtUser.getId());
        assertEquals(user.getUsername(), jwtUser.getUsername());
        assertNull(jwtUser.getPassword());
        assertNull(jwtUser.getAccessTill());
        assertNull(jwtUser.getValidTill());

        assertEquals(4, jwtUser.getAuthorities().size());
        for (JwtGrantedAuthority authority : jwtUser.getAuthorities()) {
            boolean isFound = false;
            for (GrantedPermission permission : permissions) {
                if (permission.getAuthority().equals(authority.getAuthority())) {
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                fail("Permission not found: " + authority.getAuthority());
            }
        }
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadNonExistingUser() throws Exception {
        //Act
        userDetailsService.loadUserByUsername("");

        //Assert
        fail("User doesn't exist");
    }

}