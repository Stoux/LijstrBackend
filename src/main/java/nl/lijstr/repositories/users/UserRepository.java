package nl.lijstr.repositories.users;

import java.util.List;

import nl.lijstr.domain.users.GrantedPermission;
import nl.lijstr.domain.users.Permission;
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

    /**
     * Try to find a user by their username or their email.
     *
     * @param username The username
     * @param email    The email
     *
     * @return The user or null
     */
    User findByUsernameOrEmail(String username, String email);

    /**
     * Try to find a user by their username and email.
     * This requires both fields to match where as {@link #findByUsernameOrEmail(String, String)} only requires one
     * to match.
     *
     * @param username The username
     * @param email    The email
     *
     * @return The user or null
     */
    User findByUsernameAndEmail(String username, String email);

    /**
     * Find all users that have a certain {@link Permission}.
     *
     * @param permissionName The name of the permission.
     *
     * @return the list of users
     */
    List<User> findByGrantedPermissionsPermissionName(String permissionName);

    /**
     * Find all users that don't have any email settings yet but do have a given permission.
     *
     * @param permissionName The permission to look for
     *
     * @return list of users
     */
    List<User> findByEmailSettingsIsNullAndGrantedPermissionsPermissionName(String permissionName);

}
