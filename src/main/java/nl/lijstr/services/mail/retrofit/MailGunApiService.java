package nl.lijstr.services.mail.retrofit;

import java.util.Map;
import nl.lijstr.configs.RetrofitEndpoints;
import nl.lijstr.services.mail.model.MailGunResponse;
import nl.lijstr.services.retrofit.annotations.RetrofitServiceAnnotation;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * A Retrofit service for the MailGun API.
 */
@RetrofitServiceAnnotation(
        value = RetrofitEndpoints.MAILGUN,
        interceptorClass = MailGunInterceptor.class,
        springInjectInterceptor = true
)
public interface MailGunApiService {

    /**
     * Send a mail.
     *
     * @param fieldMap The map with fields
     *
     * @return a MailGun response
     * @see <a href="https://documentation.mailgun.com/api-sending.html#sending">MailGun#Sending</a>
     */
    @FormUrlEncoded
    @POST("/messages")
    Call<MailGunResponse> sendMail(@FieldMap Map<String, String> fieldMap);

}
