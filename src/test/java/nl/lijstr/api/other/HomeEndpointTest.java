package nl.lijstr.api.other;

import nl.lijstr.beans.AppInfoBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static nl.lijstr._TestUtils.TestUtils.insertMocks;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Leon Stam on 28-4-2016.
 */
public class HomeEndpointTest {

    private AppInfoBean appInfoBean;
    private HomeEndpoint homeEndpoint;

    @BeforeEach
    public void setUp() throws Exception {
        homeEndpoint = new HomeEndpoint();
        appInfoBean = new AppInfoBean();

        insertMocks(homeEndpoint, appInfoBean);
    }

    @Test
    public void about() throws Exception {
        //Arrange
        String description = "description";
        String version = "version";

        ReflectionTestUtils.setField(appInfoBean, "description", description);
        ReflectionTestUtils.setField(appInfoBean, "buildVersion", version);
        ReflectionTestUtils.invokeMethod(appInfoBean, "fillContributors");

        //Act
        final AppInfoBean about = homeEndpoint.about();

        //Assert
        assertEquals(appInfoBean, about);
        assertEquals(1, about.getAuthors().size());
        assertEquals(3, about.getContributors().size());
        assertEquals(description, about.getDescription());
        assertEquals(version, about.getBuildVersion());

        final AppInfoBean.Contributor author = about.getAuthors().get(0);
        assertNotNull(author.getContact());
        assertNotNull(author.getName());
        assertNotNull(author.getWebsite());
        assertNotNull(author.getTasks());
        assertNotEquals(0, author.getTasks().length);

    }

}