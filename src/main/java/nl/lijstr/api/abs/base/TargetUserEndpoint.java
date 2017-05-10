package nl.lijstr.api.abs.base;

import java.util.List;
import java.util.stream.Collectors;
import nl.lijstr.api.abs.AbsService;
import nl.lijstr.api.users.models.UserSummary;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.repositories.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint for target users.
 */
public class TargetUserEndpoint extends AbsService {

    private final UserRepository userRepository;
    private final String permission;

    public TargetUserEndpoint(UserRepository userRepository, String permission) {
        this.userRepository = userRepository;
        this.permission = permission;
    }

    /**
     * Get a list of users who are allowed to fill in target data.
     *
     * @return the users
     */
    @RequestMapping
    public List<UserSummary> movieUsers() {
        return userRepository.findByGrantedPermissionsPermissionName(permission).stream()
                .map(UserSummary::convert)
                .collect(Collectors.toList());
    }

}
