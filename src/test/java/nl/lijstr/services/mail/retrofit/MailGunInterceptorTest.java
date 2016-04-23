package nl.lijstr.services.mail.retrofit;

import java.nio.charset.Charset;
import okhttp3.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.util.Base64Utils;

import static nl.lijstr._TestUtils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 23/04/2016.
 */
public class MailGunInterceptorTest {

    public static final String EXAMPLE_COM = "example.com";
    public static final String TEST_KEY = "testKey";

    private MockWebServer mockWebServer;
    private MailGunInterceptor mailGunInterceptor;

    @Before
    public void setUp() throws Exception {
        mailGunInterceptor = new MailGunInterceptor();
        ReflectionTestUtils.setField(mailGunInterceptor, "apiKey", TEST_KEY);
        ReflectionTestUtils.setField(mailGunInterceptor, "domain", EXAMPLE_COM);

        mockWebServer = new MockWebServer();
    }

    @Test
    public void intercept() throws Exception {
        //Arrange
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(mailGunInterceptor)
                .build();

        HttpUrl url = mockWebServer.url("/post");

        //Act
        mockWebServer.enqueue(new MockResponse());
        client.newCall(new Request.Builder().url(url).build()).execute();
        RecordedRequest recordedRequest = mockWebServer.takeRequest();

        //Assert
        assertEquals("/v3/" + EXAMPLE_COM + "/post", recordedRequest.getPath());
        String base64 = Base64Utils.encodeToString(("api:key-" + TEST_KEY).getBytes(Charset.forName("UTF-8")));
        assertEquals("Basic " + base64, recordedRequest.getHeader("Authorization"));

    }

}