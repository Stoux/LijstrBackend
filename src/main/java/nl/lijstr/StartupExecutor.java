package nl.lijstr;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import nl.lijstr.domain.users.GrantedPermission;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.users.PermissionRepository;
import nl.lijstr.repositories.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Created by Stoux on 19/04/2016.
 */
@Component
public class StartupExecutor implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Value("${admin.email}")
    private String adminMail;
    @Value("${admin.password}")
    private String adminPassword;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        addPermissions();
        addAdmin();
    }

    private void addPermissions() {
        //Get all permissions
        List<Permission> allPermissions = permissionRepository.findAll();
        Set<String> requiredPermissions = new HashSet<>(Arrays.asList(Permission.list()));

        //Check which ones have been made already
        for (Permission allPermission : allPermissions) {
            requiredPermissions.remove(allPermission.getName());
        }

        //Add any new ones
        for (String requiredPermission : requiredPermissions) {
            permissionRepository.saveAndFlush(new Permission(requiredPermission));
        }
    }

    private void addAdmin() {
        if (userRepository.exists(1L)) {
            return;
        }

        User user = new User("admin", "Admin", adminMail);
        user.setId(1L);
        user.setHashedPassword(passwordEncoder.encode(adminPassword));
        User admin = userRepository.saveAndFlush(user);

        String[] perms = new String[]{Permission.ADMIN, Permission.USER};
        for (String name : perms) {
            Permission permission = permissionRepository.findByName(name);
            admin.getGrantedPermissions().add(new GrantedPermission(user, permission));
        }
    }

}
