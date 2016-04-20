package nl.lijstr;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.repositories.users.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Created by Stoux on 19/04/2016.
 */
@Component
public class StartupExecutor implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        addPermissions();
    }

    private void addPermissions() {
        //Get all permissions
        List<Permission> allPermissions = permissionRepository.findAll();
        HashSet<String> requiredPermissions = new HashSet<>(Arrays.asList(Permission.list()));

        //Check which ones have been made already
        for (Permission allPermission : allPermissions) {
            requiredPermissions.remove(allPermission.getName());
        }

        //Add any new ones
        for (String requiredPermission : requiredPermissions) {
            permissionRepository.saveAndFlush(new Permission(requiredPermission));
        }
    }

}
