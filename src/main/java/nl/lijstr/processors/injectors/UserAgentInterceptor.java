package nl.lijstr.processors.injectors;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * An OkHttp Interceptor that adds the UserAgent to the request.
 */
public class UserAgentInterceptor implements Interceptor {

    private static final String HEADER = "User-Agent";

    private final String userAgent;

    public UserAgentInterceptor(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (request.header(HEADER) != null) {
            return chain.proceed(request);
        }

        Request newRequest = request.newBuilder()
            .addHeader(HEADER, userAgent)
            .build();

        return chain.proceed(newRequest);
    }

}
