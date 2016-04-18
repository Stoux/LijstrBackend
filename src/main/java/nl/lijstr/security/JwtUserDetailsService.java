package nl.lijstr.security;

import java.util.List;
import java.util.stream.Collectors;
import nl.lijstr.domain.users.GrantedPermission;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.users.UserRepository;
import nl.lijstr.security.model.JwtGrantedAuthority;
import nl.lijstr.security.model.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Created by Stoux on 18/04/2016.
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            return new JwtUser(
                    user.getId(),
                    user.getUsername(),
                    user.getHashedPassword(),
                    convertAuthorities(user.getGrantedPermissions()),
                    null,
                    null,
                    user.getValidatingKey()
            );
        }
    }

    private static List<JwtGrantedAuthority> convertAuthorities(List<GrantedPermission> grantedPermissions) {
        return grantedPermissions.stream()
                .map(grantedPermission -> new JwtGrantedAuthority(grantedPermission.getAuthority()))
                .collect(Collectors.toList());
    }

}
