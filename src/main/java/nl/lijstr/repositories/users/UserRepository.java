package nl.lijstr.repositories.users;

import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.abs.BasicRepository;

/**
 * The basic User repository.
 */
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
