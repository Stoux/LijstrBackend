package nl.lijstr.api.movies;

import nl.lijstr.api.abs.AbsService;
import nl.lijstr.api.users.models.UserSummary;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.repositories.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Endpoint for movie users.
 */
@RestController
@RequestMapping(value = "/movies/users/", produces = "application/json")
public class MovieUserEndpoint extends AbsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Get a list of users who are allowed to fill in Movie data.
     *
     * @return the users
     */
    @RequestMapping
    public List<UserSummary> movieUsers() {
        return userRepository.findByGrantedPermissionsPermissionName(Permission.MOVIE_USER).stream()
                .map(UserSummary::convert)
                .collect(Collectors.toList());
    }

}
