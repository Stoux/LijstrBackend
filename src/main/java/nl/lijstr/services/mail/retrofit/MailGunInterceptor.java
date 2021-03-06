package nl.lijstr.services.mail.retrofit;

import java.io.IOException;
import java.nio.charset.Charset;
import lombok.*;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Base64Utils;

/**
 * Created by Stoux on 22/04/2016.
 */
@NoArgsConstructor
public class MailGunInterceptor implements Interceptor {

    @Value("${mailgun.api-key}")
    private String apiKey;
    @Value("${mailgun.domain}")
    private String domain;

    private String basicAuth;

    @Override
    public Response intercept(Chain chain) throws IOException {

        //Generate the basic authentication
        if (basicAuth == null) {
            basicAuth = Base64Utils.encodeToString(("api:key-" + apiKey).getBytes(Charset.forName("UTF-8")));
        }

        //Get the original request & modify the URL
        Request originalRequest = chain.request();
        HttpUrl originalUrl = originalRequest.url();
        String newPath = "/v3/" + domain + originalUrl.encodedPath();
        HttpUrl newUrl = originalUrl.newBuilder()
                .encodedPath(newPath)
                .build();

        //Build the new request (Adds basic auth & new URL)
        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", "Basic " + basicAuth)
                .url(newUrl)
                .method(originalRequest.method(), originalRequest.body())
                .build();

        return chain.proceed(newRequest);
    }

}
