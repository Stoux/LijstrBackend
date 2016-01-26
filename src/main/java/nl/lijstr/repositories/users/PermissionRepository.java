package nl.lijstr.repositories.users;

import nl.lijstr.domain.users.GrantedPermission;
import nl.lijstr.repositories.abs.BasicMultipleUserRepository;
import org.springframework.stereotype.Repository;

/**
 * The basic GrantedPermission repository.
 */
@Repository
public interface PermissionRepository extends BasicMultipleUserRepository<GrantedPermission> {

}
