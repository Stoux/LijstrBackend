package nl.lijstr;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@Disabled
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = LijstrBackendApplication.class)
@WebAppConfiguration
public class LijstrBackendApplicationTests {

    @Test
    public void contextLoads() {
    }

}
