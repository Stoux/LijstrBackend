package nl.lijstr.services.mail.retrofit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Stoux on 23/04/2016.
 */
public class MailGunInterceptorTest {

    public static final String EXAMPLE_COM = "example.com";
    public static final String TEST_KEY = "testKey";

    private MockWebServer mockWebServer;
    private MailGunInterceptor mailGunInterceptor;

    @BeforeEach
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
        String base64 = Base64.getEncoder().encodeToString(("api:key-" + TEST_KEY).getBytes(StandardCharsets.UTF_8));
        assertEquals("Basic " + base64, recordedRequest.getHeader("Authorization"));

    }

}