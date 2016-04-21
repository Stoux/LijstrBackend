package nl.lijstr.api.users;

import nl.lijstr.api.abs.AbsCrudRestService;
import nl.lijstr.domain.users.Permission;
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
@Secured(Permission.ADMIN)
@RequestMapping(value = "/usersAdmin", produces = "application/json")
public class UsersAdminEndpoint extends AbsCrudRestService<User> {

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new UsersAdminEndpoint.
     */
    public UsersAdminEndpoint() {
        super(User.class);
    }

    @Override
    protected User validateNewItem(User newItem) {
        newItem.setId(null);
        return newItem;
    }

    @Override
    protected BasicRepository<User> getRestRepository() {
        return userRepository;
    }

}
