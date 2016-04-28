package nl.lijstr.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Created by Leon Stam on 28-4-2016.
 */
@Getter
@Component
public class AppInfoBean {

    @Value("${server.display-name}")
    private String description;

    @Value("${server.application.version}")
    private String buildVersion;

    private List<Contributor> authors;
    private List<Contributor> contributors;

    @PostConstruct
    private void fillContributors() {
        authors = Arrays.asList(
                new Contributor("Leon Stam", "admin@lijstr.nl", "https://leonstam.nl", new String[]{
                        "Project lead", "Architect", "Developer"
                })
        );
        contributors = Arrays.asList(
                new Contributor("Rick Fontein", null, "https://telluur.com", new String[]{
                        "Creative input"
                }),
                new Contributor("Lorenzo van Leeuwaarden", null, "http://mrbunni.nl", new String[]{
                        "Creative input"
                }),
                new Contributor("Erwin Stam", null, null, new String[]{
                        "Creative input"
                })
        );
    }

    @Getter
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public class Contributor {
        private String name;
        private String contact;
        private String website;
        private String[] tasks;
    }

}
