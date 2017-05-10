package nl.lijstr.services.tvmaze.retrofit;

import nl.lijstr.configs.RetrofitEndpoints;
import nl.lijstr.services.retrofit.annotations.RetrofitServiceAnnotation;
import nl.lijstr.services.tvmaze.models.TvmShow;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Endpoints for TvMaze.
 */
@RetrofitServiceAnnotation(value = RetrofitEndpoints.TV_MAZE, readTimeout = 30)
public interface TvMazeApi {

    /**
     * Get a Show from TvMaze.
     *
     * @param id The TvMaze ID of the show
     *
     * @return the {@link TvmShow} call
     */
    @GET("/shows/{id}?embed[]=episodes&embed[]=seasons")
    Call<TvmShow> getShow(@Path("id") long id);

    /**
     * Try to find a Show by it's IMDB ID.
     * <p>
     * Warning: This does not return embedded results.
     *
     * @param imdbId The IMDB ID
     *
     * @return the {@link TvmShow} call
     */
    @GET("/lookup/shows")
    Call<TvmShow> lookupByImdbId(@Query("imdb") String imdbId);

}
