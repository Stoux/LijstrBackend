package nl.lijstr.api.users;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import nl.lijstr.api.abs.AbsService;
import nl.lijstr.api.users.models.*;
import nl.lijstr.common.Utils;
import nl.lijstr.domain.other.ApprovedFor;
import nl.lijstr.domain.users.GrantedPermission;
import nl.lijstr.domain.users.LoginAttempt;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.BadRequestException;
import nl.lijstr.exceptions.security.UnauthorizedException;
import nl.lijstr.repositories.users.LoginAttemptRepository;
import nl.lijstr.repositories.users.PermissionRepository;
import nl.lijstr.repositories.users.UserRepository;
import nl.lijstr.security.model.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * The Users Endpoint.
 */
@Secured(Permission.USER)
@RestController
@RequestMapping(value = "/users", produces = "application/json")
public class UserEndpoint extends AbsService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginAttemptRepository loginAttemptRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Get all users.
     *
     * @return list of users
     */
    @Secured(Permission.ADMIN)
    @RequestMapping()
    public List<UserDetails> getUsers() {
        return userRepository.findAll().stream()
            .map(user -> new UserDetails(
                user,
                this.loginAttemptRepository.findFirstByUserAndSuccessOrderByTimestampDesc(user, true),
                this.loginAttemptRepository.findFirstByUserAndSuccessOrderByTimestampDesc(user, false)
            ))
            .collect(Collectors.toList());
    }


    /**
     * Get the user details of a user.
     *
     * @param id The user's ID
     *
     * @return the user
     */
    @RequestMapping("/{id:\\d+}")
    public User getUserDetails(@PathVariable Long id) {
        checkUserOrAdmin(id);
        return findOne(userRepository, id, "User");
    }

    /**
     * Update the general details of the user.
     *
     * @param id                ID of the user
     * @param updateUserRequest Updated fields
     *
     * @return the updated user
     */
    @RequestMapping(value = "/{id:\\d+}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public User updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        checkUserOrAdmin(id);
        User foundUser = findOne(userRepository, id, "User");
        updateUserRequest.applyTo(foundUser);
        return userRepository.saveAndFlush(foundUser);
    }

    /**
     * Get all permissions the user has.
     *
     * @param id The user's ID
     *
     * @return the list of permissions
     */
    @RequestMapping("/{id:\\d+}/permissions")
    public List<GrantedPermission> getPermissions(@PathVariable Long id) {
        checkUserOrAdmin(id);
        return findOne(userRepository, id, "User").getGrantedPermissions();
    }

    /**
     * Modify the permissions of a user.
     *
     * @param id             The user's ID
     * @param permissionList The list of permissions
     *
     * @return the list of granted permissions
     */
    @Secured(Permission.ADMIN)
    @RequestMapping(value = "/{id:\\d+}/permissions", method = RequestMethod.PUT)
    public List<GrantedPermission> modifyPermissions(@PathVariable Long id, @RequestBody PermissionList permissionList) {
        //Validate permissions
        Set<String> allPermissions = Arrays.stream(Permission.list()).collect(Collectors.toSet());
        for (String givenPermission : permissionList.getPermissions()) {
            if (!allPermissions.contains(givenPermission)) {
                throw new BadRequestException("Invalid permission: " + givenPermission);
            }
        }

        //Get the user in question
        User user = findOne(userRepository, id, "User");
        List<GrantedPermission> permissions = user.getGrantedPermissions();
        Utils.updateList(
                permissions, permissionList.getPermissions(), GrantedPermission::getAuthority,
                permission -> new GrantedPermission(
                        user,
                        permissionRepository.findByName(permission)
                )
        );
        userRepository.saveAndFlush(user);

        return permissions;
    }

    /**
     * Get a list of all available permissions.
     *
     * @return permissions
     */
    @Secured(Permission.ADMIN)
    @RequestMapping("/permissions")
    public List<Permission> getAvailablePermissions() {
        return permissionRepository.findAll();
    }

    /**
     * List all available 'approvedFor' options.
     *
     * @return List of options
     */
    @Secured(Permission.ADMIN)
    @RequestMapping("/list-approved-for")
    public List<ApprovedFor> getAvailableApprovedForIds() {
        return Arrays.asList(ApprovedFor.values());
    }


    /**
     * Allows a user to change their password.
     *
     * @param id            The user's ID
     * @param changeRequest The change request
     */
    @RequestMapping(value = "/{id:\\d+}/changePassword", method = RequestMethod.PUT)
    public void changePassword(@PathVariable Long id, @Valid @RequestBody PasswordChangeRequest changeRequest) {
        //Get the user
        JwtUser jwtuser = getUser();
        if (!id.equals(jwtuser.getId())) {
            throw new UnauthorizedException();
        }
        User user = findOne(userRepository, id, "User");

        //Check the current password
        if (!passwordEncoder.matches(changeRequest.getCurrentPassword(), user.getHashedPassword())) {
            throw new BadRequestException("Password doesn't match");
        }

        //Set the new password
        user.setHashedPassword(passwordEncoder.encode(changeRequest.getNewPassword()));
        user.incrementValidatingKey();
        userRepository.save(user);

        //TODO: Mail the user
    }

    /**
     * Add a new {@link User}.
     *
     * @param userRequest The create user request
     *
     * @return the user
     */
    @Secured(Permission.ADMIN)
    @RequestMapping(value = "/", method = RequestMethod.POST, consumes = "application/json")
    public User addUser(@Valid @RequestBody CreateUserRequest userRequest) {
        User foundUser = userRepository.findByUsernameOrEmail(userRequest.getUsername(), userRequest.getEmail());
        if (foundUser != null) {
            throw new BadRequestException("Username or email already exists");
        }

        User newUser = new User(
                userRequest.getUsername(), userRequest.getDisplayName(), userRequest.getEmail(),
                userRequest.getApprovedFor() == null ? ApprovedFor.EVERYONE : userRequest.getApprovedFor()
        );

        return userRepository.saveAndFlush(newUser);
        //TODO: Send a mail
    }

    /**
     * Check if the user is either the requested user or an admin.
     *
     * @param id The user's ID
     */
    private void checkUserOrAdmin(Long id) {
        if (id == null) {
            throw new BadRequestException("ID is null");
        }

        JwtUser user = getUser();
        if (!id.equals(user.getId())) {
            checkPermission(user, Permission.ADMIN);
        }
    }


}
