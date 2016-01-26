package nl.lijstr.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The Database configuration used by the Spring application.
 */
@Configuration
@EnableJpaRepositories("nl.lijstr")
public class DatabaseConfig {


}
