package nl.lijstr.api.users;

import nl.lijstr.api.abs.AbsRestService;
import nl.lijstr.configs.security.Permissions;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.abs.BasicRepository;
import nl.lijstr.repositories.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Users Endpoint for Admins.
 * <p>
 * Can be used by admins to control user data.
 */
@RestController
@Secured(Permissions.ADMIN)
@RequestMapping(value = "/users", produces = "application/json")
public class UsersAdminEndpoint extends AbsRestService<User> {

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new UsersAdminEndpoint.
     */
    public UsersAdminEndpoint() {
        super("User");
    }

    @Override
    protected BasicRepository<User> getRestRepository() {
        return userRepository;
    }

}
