package nl.lijstr.api.abs;

import java.util.Map;
import nl.lijstr.common.Utils;
import nl.lijstr.domain.base.IdModel;
import nl.lijstr.exceptions.db.NotFoundException;
import nl.lijstr.exceptions.security.UnauthorizedException;
import nl.lijstr.repositories.abs.BasicRepository;
import nl.lijstr.security.model.JwtGrantedAuthority;
import nl.lijstr.security.model.JwtUser;
import nl.lijstr.security.spring.JwtAuthenticationToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * The base AbsService used for {@link org.springframework.web.bind.annotation.RestController}s.
 */
@CrossOrigin
public abstract class AbsService {

    /**
     * Create an OK Response with the given message.
     *
     * @param message The message
     *
     * @return the response
     */
    protected ResponseEntity<Map> ok(String message) {
        return new ResponseEntity<>(
                Utils.asMap("message", message),
                HttpStatus.OK
        );
    }

    /**
     * Find an item by it's ID in the given repository.
     * Throws an NotFoundException if not found.
     *
     * @param basicRepository The repository
     * @param id              ID of the item
     * @param itemName        The name of the item (for the NFE)
     * @param <X>             The class of the item
     *
     * @return the item
     */
    protected <X extends IdModel> X findOne(BasicRepository<X> basicRepository, long id, String itemName) {
        X item = basicRepository.findOne(id);
        if (item == null) {
            throw new NotFoundException(itemName, id);
        }
        return item;
    }

    /**
     * Check if the result is not null.
     * If the result is null throw a NotFoundException.
     *
     * @param result The result
     */
    protected void checkIfFound(Object result) {
        if (result == null) {
            throw new NotFoundException();
        }
    }

    /**
     * Check if the result is not null.
     * If the result is null throw a NotFoundException for a certain item /w key.
     *
     * @param result   The result
     * @param itemName The name of the item
     * @param key      The key for the item
     */
    protected void checkIfFound(Object result, String itemName, String key) {
        if (result == null) {
            throw new NotFoundException(itemName, key);
        }
    }

    /**
     * Get the currently logged in user from the Security context.
     *
     * @return the user
     */
    protected JwtUser getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication instanceof JwtAuthenticationToken) {
            return ((JwtAuthenticationToken) authentication).getJwtUser();
        } else {
            throw new AuthenticationCredentialsNotFoundException("No JSON Web Tokens user found");
        }
    }

    /**
     * Check if a user has a certain permission.
     *
     * @param jwtUser    The user
     * @param permission The permission (use {@link nl.lijstr.domain.users.Permission} statics)
     */
    protected void checkPermission(JwtUser jwtUser, String permission) {
        for (JwtGrantedAuthority authority : jwtUser.getAuthorities()) {
            if (authority.getAuthority().equals(permission)) {
                return;
            }
        }
        throw new UnauthorizedException();
    }

}
