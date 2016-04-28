package nl.lijstr.api.movies;

import java.util.ArrayList;
import java.util.List;
import nl.lijstr._TestUtils.TestUtils;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.users.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static nl.lijstr._TestUtils.TestUtils.*;
import static org.mockito.Mockito.*;

/**
 * Created by Leon Stam on 28-4-2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class MovieUserEndpointTest {

    @Mock
    private UserRepository userRepository;

    private MovieUserEndpoint endpoint;

    @Before
    public void setUp() throws Exception {
        endpoint = new MovieUserEndpoint();
        insertMocks(endpoint, userRepository);
    }

    @Test
    public void movieUsers() throws Exception {
        //Arrange
        String permission = Permission.MOVIE_USER;
        List<User> users = new ArrayList<>();
        User user = new User(1L);
        users.add(user);

        when(userRepository.findByGrantedPermissionsPermissionName(eq(permission))).thenReturn(users);

        //Act
        List<User> foundUsers = endpoint.movieUsers();

        //Assert
        assertNotNull(foundUsers);
        assertEquals(users, foundUsers);
        assertEquals(user, foundUsers.get(0));
    }

}