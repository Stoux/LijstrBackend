package nl.lijstr.services.omdb.retrofit;

import nl.lijstr.configs.RetrofitEndpoints;
import nl.lijstr.services.omdb.models.OmdbObject;
import nl.lijstr.services.omdb.models.OmdbSearchContainer;
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
     * @param apiKey The API key
     *
     * @return The OmdbObject call
     */
    @GET("/")
    Call<OmdbObject> getByImdbId(@Query("i") String imdbId, @Query("apikey") String apiKey);

    /**
     * Search IMDB for entries.
     *
     * @param searchQuery The search query (title)
     * @param type Type of object (movie, series, episode)
     * @param apiKey The API key
     *
     * @return list of search results
     */
    @GET("/")
    Call<OmdbSearchContainer> search(@Query("s") String searchQuery, @Query("type") String type, @Query("apikey") String apiKey);

}
