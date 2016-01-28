package nl.lijstr.services.retrofit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * A service that provides Retrofit instances for multiple endpoints.
 */
@Service
public class RetrofitService {

    private Map<String, Retrofit> endpointMap;

    /**
     * Create a RetrofitService.
     */
    public RetrofitService() {
        endpointMap = new ConcurrentHashMap<>();
    }

    /**
     * Get a Retrofit endpoint.
     *
     * @param endpoint the endpoint URL
     *
     * @return the Retrofit instance
     */
    public Retrofit getRetrofitEndpoint(String endpoint) {
        //Check if cached
        if (endpointMap.containsKey(endpoint)) {
            return endpointMap.get(endpoint);
        }

        //Create it
        Retrofit retrofit = createRetrofit(endpoint);
        endpointMap.put(endpoint, retrofit);
        return retrofit;
    }

    private Retrofit createRetrofit(String endpoint) {
        return new Retrofit.Builder()
                .baseUrl(endpoint)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}
