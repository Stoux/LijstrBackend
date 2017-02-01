package nl.lijstr.services.migrate.retrofit;

import java.util.List;
import java.util.Map;
import nl.lijstr.configs.RetrofitEndpoints;
import nl.lijstr.services.migrate.models.movies.OldMovie;
import nl.lijstr.services.migrate.models.movies.OldMovieRating;
import nl.lijstr.services.retrofit.annotations.RetrofitServiceAnnotation;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Endpoint for reaching details on the old site.
 */
@RetrofitServiceAnnotation(RetrofitEndpoints.OLD_SITE)
public interface OldSiteService {

    /**
     * Get all movies on the old site.
     *
     * @return list of movies
     */
    @GET("/film/json/list.php")
    Call<Map<Long, OldMovie>> listMovies();

    /**
     * Get the old ratings for a certain movie.
     *
     * @param movieId The ID of the movie (on the old site)
     * @return map of names to ratings
     */
    @GET("/film/json/ratings.php")
    Call<Map<String, OldMovieRating>> getRatings(@Query("id") Long movieId);


}
