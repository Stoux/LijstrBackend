package nl.lijstr.services.retrofit;

import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import retrofit.Call;
import retrofit.Retrofit;
import retrofit.http.GET;

import static nl.lijstr._TestUtils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 29/01/2016.
 */
public class RetrofitServiceTest {

    public static final String ENDPOINT_1 = "http://example.com";
    public static final String ENDPOINT_2 = "http://www.example2.com";
    private RetrofitService retrofitService;

    //Mirror'd from RetrofitService
    private Map<String, Retrofit> endpointMap;

    @Before
    public void setUp() throws Exception {
        retrofitService = new RetrofitService();
        endpointMap = getFieldValue(retrofitService, "endpointMap");
    }

    @Test
    public void testCreate() throws Exception {
        //Act
        TestEndpoint endpoint = retrofitService.createRetrofitService(ENDPOINT_1, TestEndpoint.class);

        //Assert
        assertNotNull(endpoint);
        assertEquals(1, endpointMap.size());
        assertTrue(endpointMap.containsKey(ENDPOINT_1));
    }

    @Test
    public void testCreateUseTwice() throws Exception {
        //Act
        TestEndpoint xEndpoint = retrofitService.createRetrofitService(ENDPOINT_1, TestEndpoint.class);
        TestEndpoint yEndpoint = retrofitService.createRetrofitService(ENDPOINT_1, TestEndpoint.class);

        //Assert
        assertNotNull(xEndpoint);
        assertNotNull(yEndpoint);
        assertNotEquals(xEndpoint, yEndpoint);
        assertEquals(1, endpointMap.size());
    }

    @Test
    public void testCreateMultiple() throws Exception {
        //Act
        TestEndpoint endpoint1 = retrofitService.createRetrofitService(ENDPOINT_1, TestEndpoint.class);
        TestEndpoint endpoint2 = retrofitService.createRetrofitService(ENDPOINT_2, TestEndpoint.class);

        //Assert
        assertNotNull(endpoint1);
        assertNotNull(endpoint2);
        assertNotEquals(endpoint1, endpoint2);
        assertEquals(2, endpointMap.size());
        assertTrue(endpointMap.containsKey(ENDPOINT_1));
        assertTrue(endpointMap.containsKey(ENDPOINT_2));
    }

    public interface TestEndpoint {
        @GET("/")
        Call<String> makeCall();
    }

}