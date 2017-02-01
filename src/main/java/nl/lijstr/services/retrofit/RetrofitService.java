package nl.lijstr.services.retrofit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import nl.lijstr.services.retrofit.models.TimeoutTimings;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
     * Create a Retrofit Service.
     *
     * @param endpoint     The endpoint URL
     * @param xClass       The service class
     * @param interceptors One or more interceptors
     * @param <X>          the service
     *
     * @return the service
     */
    public <X> X createRetrofitService(String endpoint, Class<X> xClass, TimeoutTimings timings,
                                       Interceptor... interceptors) {
        Retrofit retrofit = getRetrofitEndpoint(endpoint, timings, interceptors);
        return retrofit.create(xClass);
    }

    private Retrofit getRetrofitEndpoint(String endpoint, TimeoutTimings timings, Interceptor[] interceptors) {
        //Check if cached
        if (endpointMap.containsKey(endpoint)) {
            return endpointMap.get(endpoint);
        }

        //Create it
        Retrofit retrofit = createRetrofit(endpoint, timings, interceptors);
        endpointMap.put(endpoint, retrofit);
        return retrofit;
    }

    private Retrofit createRetrofit(String endpoint, TimeoutTimings timings, Interceptor[] interceptors) {
        //Create the OkHttpClient
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        //Add interceptors
        for (Interceptor customInterceptor : interceptors) {
            clientBuilder.addInterceptor(customInterceptor);
        }
        clientBuilder.addInterceptor(interceptor);

        //Add timings
        clientBuilder.connectTimeout((long) timings.getConnect(), TimeUnit.SECONDS)
                .readTimeout((long) timings.getRead(), TimeUnit.SECONDS)
                .writeTimeout((long) timings.getWrite(), TimeUnit.SECONDS);

        //Build the Retrofit instance
        return new Retrofit.Builder()
                .baseUrl(endpoint)
                .addConverterFactory(GsonConverterFactory.create())
                .client(clientBuilder.build())
                .build();
    }

}
