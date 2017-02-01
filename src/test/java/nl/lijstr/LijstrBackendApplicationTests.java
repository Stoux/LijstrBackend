package nl.lijstr;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LijstrBackendApplication.class)
@WebAppConfiguration
public class LijstrBackendApplicationTests {

    @Test
    public void contextLoads() {
    }

}
