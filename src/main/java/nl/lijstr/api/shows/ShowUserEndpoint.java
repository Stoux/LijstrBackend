package nl.lijstr.api.shows;

import nl.lijstr.api.abs.base.TargetUserEndpoint;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.repositories.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint for show users.
 */
@RestController
@RequestMapping(value = "/shows/users/", produces = "application/json")
public class ShowUserEndpoint extends TargetUserEndpoint {

    @Autowired
    public ShowUserEndpoint(UserRepository userRepository) {
        super(userRepository, Permission.SHOW_USER);
    }

}
