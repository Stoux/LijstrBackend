package nl.lijstr.repositories.users;

import nl.lijstr.domain.users.Permission;
import nl.lijstr.repositories.abs.BasicRepository;

/**
 * The basic Permission repository.
 */
public interface PermissionRepository extends BasicRepository<Permission> {

    /**
     * Find a Permission by it's... permission.
     *
     * @param permission The permission
     *
     * @return also the permission.. Naming.
     */
    Permission findByPermission(String permission);

}
