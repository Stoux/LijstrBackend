package nl.lijstr.services.omdb.retrofit;

import nl.lijstr.configs.RetrofitEndpoints;
import nl.lijstr.services.omdb.models.OmdbObject;
import nl.lijstr.services.retrofit.annotations.RetrofitServiceAnnotation;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Leon Stam on 21-4-2016.
 */
@RetrofitServiceAnnotation(RetrofitEndpoints.OMDB)
public interface OmdbService {

    /**
     * Get a Movie/Series by their IMDB ID.
     *
     * @param imdbId The ID
     *
     * @return The OmdbObject call
     */
    @GET("/")
    Call<OmdbObject> getByImdbId(@Query("i") String imdbId);


}
