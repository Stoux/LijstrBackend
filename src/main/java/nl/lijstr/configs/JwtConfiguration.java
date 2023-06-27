package nl.lijstr.configs;

import nl.lijstr.security.JwtUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;

@Configuration
public class JwtConfiguration {



    @Bean
    public AuthenticationManager configureAuthenticationManager(PasswordEncoder passwordEncoder, UserDetailsService detailsService) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(detailsService);
        authProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authProvider);
    }



    /**
     * Creates a {@link PasswordEncoder}.
     *
     * @return The encoder
     */
    @Bean
    public PasswordEncoder passwordEncoderBean() {
        return new BCryptPasswordEncoder();
    }


    /**
     * Creates a {@link SecureRandom}.
     *
     * @return the random
     */
    @Bean
    public SecureRandom secureRandomBean() {
        return new SecureRandom();
    }

}
