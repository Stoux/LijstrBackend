package nl.lijstr.services.kanye.retrofit;

import nl.lijstr.services.kanye.models.KanyeQuote;
import nl.lijstr.services.retrofit.annotations.RetrofitServiceAnnotation;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Service defintion for the kanye.rest endpoints.
 */
@RetrofitServiceAnnotation(value = "https://api.kanye.rest", readTimeout = 300)
public interface KanyeRestService {


    @GET("/")
    Call<KanyeQuote> getQuote();

}
