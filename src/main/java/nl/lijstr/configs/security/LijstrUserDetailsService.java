package nl.lijstr.configs.security;

import java.util.Collection;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.users.PermissionRepository;
import nl.lijstr.repositories.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * The Lijstr UserDetailsService.
 * <p>
 * Supplies the Security node with users.
 */
@Service
public class LijstrUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username '" + username + "' was not found");
        }

        return asSpringUser(user);
    }

    private org.springframework.security.core.userdetails.User asSpringUser(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                getRights(user)
        );
    }

    private Collection<? extends GrantedAuthority> getRights(User user) {
        return permissionRepository.findByUser(user);
    }

}
