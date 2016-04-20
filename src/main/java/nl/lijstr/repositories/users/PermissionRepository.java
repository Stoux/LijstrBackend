package nl.lijstr.repositories.users;

import nl.lijstr.domain.users.Permission;
import nl.lijstr.repositories.abs.BasicRepository;

/**
 * The basic Permission repository.
 */
public interface PermissionRepository extends BasicRepository<Permission> {

    /**
     * Find a Permission.
     *
     * @param permissionName The name
     *
     * @return the permission or null
     */
    Permission findByName(String permissionName);

}
