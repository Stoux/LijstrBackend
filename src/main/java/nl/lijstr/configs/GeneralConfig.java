package nl.lijstr.configs;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The Database configuration used by the Spring application.
 */
@Configuration
@EnableCaching
@EnableScheduling
@EnableJpaRepositories("nl.lijstr")
public class GeneralConfig {


}
