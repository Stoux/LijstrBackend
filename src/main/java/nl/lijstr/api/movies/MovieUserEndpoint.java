package nl.lijstr.api.movies;

import nl.lijstr.api.abs.base.TargetUserEndpoint;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.repositories.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint for movie users.
 */
@RestController
@RequestMapping(value = "/movies/users/", produces = "application/json")
public class MovieUserEndpoint extends TargetUserEndpoint {

    @Autowired
    public MovieUserEndpoint(UserRepository userRepository) {
        super(userRepository, Permission.MOVIE_USER);
    }

}
