package nl.lijstr.services.retrofit;

import nl.lijstr.beans.AppInfoBean;
import nl.lijstr.services.retrofit.models.TimeoutTimings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;

import java.util.Map;

import static nl.lijstr._TestUtils.TestUtils.getFieldValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Stoux on 29/01/2016.
 */
public class RetrofitServiceTest {

    public static final String ENDPOINT_1 = "http://example.com";
    public static final String ENDPOINT_2 = "http://www.example2.com";
    public static final TimeoutTimings TIMINGS = new TimeoutTimings(10, 10, 10);
    private RetrofitService retrofitService;

    //Mirror'd from RetrofitService
    private Map<String, Retrofit> endpointMap;

    @BeforeEach
    public void setUp() throws Exception {
        AppInfoBean infoBean = mock(AppInfoBean.class);
        when(infoBean.getUserAgent()).thenReturn("UserAgent");

        retrofitService = new RetrofitService(infoBean);
        endpointMap = getFieldValue(retrofitService, "endpointMap");
    }

    @Test
    public void testCreate() throws Exception {
        //Act
        TestEndpoint endpoint = retrofitService.createRetrofitService(ENDPOINT_1, TestEndpoint.class, TIMINGS);

        //Assert
        assertNotNull(endpoint);
        assertEquals(1, endpointMap.size());
        assertTrue(endpointMap.containsKey(ENDPOINT_1));
    }

    @Test
    public void testCreateUseTwice() throws Exception {
        //Act
        TestEndpoint xEndpoint = retrofitService.createRetrofitService(ENDPOINT_1, TestEndpoint.class, TIMINGS);
        TestEndpoint yEndpoint = retrofitService.createRetrofitService(ENDPOINT_1, TestEndpoint.class, TIMINGS);

        //Assert
        assertNotNull(xEndpoint);
        assertNotNull(yEndpoint);
        assertNotEquals(xEndpoint, yEndpoint);
        assertEquals(1, endpointMap.size());
    }

    @Test
    public void testCreateMultiple() throws Exception {
        //Act
        TestEndpoint endpoint1 = retrofitService.createRetrofitService(ENDPOINT_1, TestEndpoint.class, TIMINGS);
        TestEndpoint endpoint2 = retrofitService.createRetrofitService(ENDPOINT_2, TestEndpoint.class, TIMINGS);

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