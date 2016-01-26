package nl.lijstr.repositories.users;

import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.abs.BasicRepository;
import org.springframework.stereotype.Repository;

/**
 * The basic User repository.
 */
@Repository
public interface UserRepository extends BasicRepository<User> {

    /**
     * Find a User by their username.
     *
     * @param username The username
     *
     * @return The user or null
     */
    User findByUsername(String username);

}
