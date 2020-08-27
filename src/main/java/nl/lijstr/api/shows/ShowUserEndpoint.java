package nl.lijstr.api.shows;

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
 * Endpoint for show users.
 */
@RestController
@RequestMapping(value = "/shows/users/", produces = "application/json")
public class ShowUserEndpoint extends AbsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Get a list of users who are allowed to fill in Show data.
     *
     * @return the users
     */
    @RequestMapping
    public List<UserSummary> showUsers() {
        return userRepository.findByGrantedPermissionsPermissionName(Permission.SHOW_USER).stream()
                .map(UserSummary::convert)
                .collect(Collectors.toList());
    }

}
