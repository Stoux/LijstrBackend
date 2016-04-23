package nl.lijstr.services.mail;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import nl.lijstr.common.Container;
import nl.lijstr.domain.other.ApprovedFor;
import nl.lijstr.domain.other.MemeImage;
import nl.lijstr.domain.other.MemeQuote;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.LijstrException;
import nl.lijstr.repositories.other.MemeImageRepository;
import nl.lijstr.repositories.other.MemeQuoteRepository;
import nl.lijstr.services.mail.model.MailGunResponse;
import nl.lijstr.services.mail.model.MailTemplate;
import nl.lijstr.services.mail.retrofit.MailGunApiService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import static nl.lijstr._TestUtils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 23/04/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class MailServiceTest {

    public static final String ADMIN_EXAMPLE_COM = "admin@example.com";
    public static final String HTTP_EXAMPLE_COM = "http://example.com";
    public static final String ADMIN_TO_EXAMPLE_COM = "admin_to@example.com";

    @Mock
    private MemeQuoteRepository memeQuoteRepository;
    @Mock
    private MemeImageRepository memeImageRepository;
    @Mock
    private MailGunApiService mailGunApiService;

    private MailService mailService;
    private boolean answerCalled = false;

    @Before
    public void setUp() throws Exception {
        mailService = new MailService();
        mockLogger(mailService);
        insertMocks(mailService, memeImageRepository, memeQuoteRepository, mailGunApiService);
        ReflectionTestUtils.setField(mailService, "from", ADMIN_EXAMPLE_COM);
        ReflectionTestUtils.setField(mailService, "appHost", HTTP_EXAMPLE_COM);
    }


    @Test
    public void postConstruct() throws Exception {
        //Arrange
        InputStream resourceStream = getTestResource("mail/mail.test.json");
        Resource mockResource = mock(Resource.class);
        when(mockResource.getInputStream())
                .thenReturn(resourceStream);
        insertMocks(mailService, mockResource);

        //Act
        ReflectionTestUtils.invokeMethod(mailService, "getTemplateMail");

        //Assert
        assertNotNull(ReflectionTestUtils.getField(mailService, "templateMail"));
    }

    @Test(expected = LijstrException.class)
    public void failedPostConstruct() throws Exception {
        //Arrange
        Resource mockResource = mock(Resource.class);
        when(mockResource.getInputStream())
                .thenThrow(new IOException());
        insertMocks(mailService, mockResource);

        //Act
        ReflectionTestUtils.invokeMethod(mailService, "getTemplateMail");

        //Assert
        fail("Coulnd't read IOStream, LijstrException should have been thrown");
    }

    @Test
    public void sendMail() throws Exception {
        //Arrange
        postConstruct();

        final String subject = "Example Subject";
        final String tag = "example-tag";
        final User user = new User("admin", "Admin", ADMIN_TO_EXAMPLE_COM, ApprovedFor.EVERYONE);
        final MailTemplate mailTemplate = new MailTemplate("Test message", "/testPath", "Do a test");
        final MailGunResponse mailGunResponse = new MailGunResponse("id", "Queued");
        final Container<String> htmlContainer = new Container<>();

        when(mailGunApiService.sendMail(any()))
                .thenAnswer(invocation -> {
                    Map<String, String> map = getInvocationParam(invocation, 0);
                    assertEquals(ADMIN_EXAMPLE_COM, map.get("from"));
                    assertEquals(ADMIN_TO_EXAMPLE_COM, map.get("to"));
                    assertEquals(subject, map.get("subject"));
                    assertEquals(tag, map.get("o:tag"));
                    assertTrue(map.get("text").startsWith("Hi " + user.getDisplayName()));
                    htmlContainer.setItem(map.get("html"));
                     answerCalled = true;
                    return successCall(mailGunResponse);
                });

        MemeImage memeImage = new MemeImage(1, 2, "src", "srcsub", ApprovedFor.EVERYONE);
        when(memeImageRepository.findRandomForApproved(any()))
                .thenReturn(Arrays.asList(memeImage));

        MemeQuote memeQuote = new MemeQuote("memeExample", ApprovedFor.EVERYONE);
        when(memeQuoteRepository.findRandomForApproved(any()))
                .thenReturn(Arrays.asList(memeQuote));


        //Act
        MailGunResponse mailResponse = mailService.sendMail(subject, user, mailTemplate, tag);

        //Assert
        assertTrue(answerCalled);
        assertEquals(mailGunResponse, mailResponse);
        assertTrue(htmlContainer.isPresent());

        //Check build mail
        Map htmlMap = new Gson().fromJson(htmlContainer.getItem(), Map.class);
        assertEquals(subject, htmlMap.get("title"));
        assertEquals(user.getDisplayName(), htmlMap.get("user"));
        assertEquals("There's new stuff!", htmlMap.get("subtitle"));
        assertEquals(mailTemplate.getMessage(), htmlMap.get("message"));
        assertEquals(mailTemplate.getButton(), htmlMap.get("button"));
        assertEquals(HTTP_EXAMPLE_COM + mailTemplate.getButtonUrlPath(), htmlMap.get("button-url"));
        assertEquals(String.valueOf(memeImage.getImgWidth()), htmlMap.get("gif-width"));
        assertEquals(String.valueOf(memeImage.getImgHeight()), htmlMap.get("gif-height"));
        assertEquals(memeImage.getImgSrc(), htmlMap.get("gif-src"));
        assertEquals(memeImage.getImgSubtitle(), htmlMap.get("gif-subtitle"));
        assertEquals(memeQuote.getQuote(), htmlMap.get("footer"));
        assertEquals(memeQuote.getQuote(), htmlMap.get("footer2"));
    }

}