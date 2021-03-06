package nl.lijstr;

import nl.lijstr.services.modify.ModelModifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * The main LijstrBackendApplication class for starting the application.
 * This is the JAR method of starting.
 */
@EntityScan("nl.lijstr")
@SpringBootApplication(scanBasePackages = "nl.lijstr")
public class LijstrBackendApplication {

    @Autowired
    private ModelModifyService service;

    /**
     * Start the application.
     *
     * @param args possible CLI args
     */
    public static void main(String[] args) {
        SpringApplication.run(LijstrBackendApplication.class, args);
    }

}
