package nl.lijstr;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * The main SpringBootServletInitializer class for starting the application.
 * This is the WAR method of starting.
 */
public class ServletInitializer extends SpringBootServletInitializer {

    @SuppressWarnings("squid:UnusedProtectedMethod")
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(LijstrBackendApplication.class);
    }

}
