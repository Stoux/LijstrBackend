package nl.lijstr.api.users;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import nl.lijstr.api.abs.AbsService;
import nl.lijstr.api.users.models.CreateUserRequest;
import nl.lijstr.api.users.models.PasswordChangeRequest;
import nl.lijstr.api.users.models.PermissionList;
import nl.lijstr.common.Utils;
import nl.lijstr.domain.users.GrantedPermission;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.BadRequestException;
import nl.lijstr.exceptions.security.UnauthorizedException;
import nl.lijstr.repositories.users.PermissionRepository;
import nl.lijstr.repositories.users.UserRepository;
import nl.lijstr.security.model.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
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
    private PasswordEncoder passwordEncoder;

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
                throw new BadRequestException("Invalid permmission: " + givenPermission);
            }
        }

        //Get the user in question
        User user = findOne(userRepository, id, "User");
        List<GrantedPermission> permissions = user.getGrantedPermissions();
        Utils.updateList(
                permissions, permissionList.getPermissions(), GrantedPermission::getAuthority,
                permission -> new GrantedPermission(
                        permissionRepository.findByName(permission)
                )
        );
        userRepository.saveAndFlush(user);

        return permissions;
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

        User newUser = new User(userRequest.getUsername(), userRequest.getDisplayName(), userRequest.getEmail());
        return userRepository.saveAndFlush(newUser);
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
