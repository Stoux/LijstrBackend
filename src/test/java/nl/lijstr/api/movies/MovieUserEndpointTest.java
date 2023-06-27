package nl.lijstr.api.movies;

import nl.lijstr.api.users.models.UserSummary;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.users.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static nl.lijstr._TestUtils.TestUtils.insertMocks;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

/**
 * Created by Leon Stam on 28-4-2016.
 */
@ExtendWith(MockitoExtension.class)
public class MovieUserEndpointTest {

    @Mock
    private UserRepository userRepository;

    private MovieUserEndpoint endpoint;

    @BeforeEach
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
        List<UserSummary> foundUsers = endpoint.movieUsers();

        //Assert
        assertNotNull(foundUsers);
        assertEquals(user.getId(), foundUsers.get(0).getId());
    }

}