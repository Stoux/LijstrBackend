package nl.lijstr.configs;

import java.security.SecureRandom;
import nl.lijstr.security.JwtUserDetailsService;
import nl.lijstr.security.spring.JwtAuthenticationEntryPoint;
import nl.lijstr.security.spring.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * The Security configuration used by the Spring application.
 */
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtUserDetailsService detailsService;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private JwtAuthenticationTokenFilter tokenFilter;

    @SuppressWarnings("squid:UnusedProtectedMethod")
    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(detailsService)
                .passwordEncoder(passwordEncoderBean());
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

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @SuppressWarnings("squid:UnusedProtectedMethod")
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                //Don't need CSRF
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

                //No sessions, full REST
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                //Authorize paths
                .authorizeRequests()

                //Explicitly allow authentication paths
                .antMatchers("/auth/**").permitAll()

                //Auth is required for...
                //=> Pretty much all POST/PUT interactions
                .antMatchers(HttpMethod.POST, "/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/**").authenticated()
                //=> Movie update triggers
                .antMatchers("/movies/update/**").access("hasRole('ROLE_ADMIN')")

                //Allow the rest
                .anyRequest().permitAll();

        //Add JWT Security filter
        httpSecurity
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);

        //Disable user side cache
        httpSecurity.headers().cacheControl();
    }
}
