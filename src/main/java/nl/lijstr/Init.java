package nl.lijstr;

import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.repositories.users.PermissionRepository;
import nl.lijstr.repositories.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A class to initialize some data.
 */
@Service
public class Init {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionRepository permissionRepository;


}
