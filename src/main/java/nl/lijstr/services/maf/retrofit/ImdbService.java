package nl.lijstr.services.maf.retrofit;

import nl.lijstr.configs.RetrofitEndpoints;
import nl.lijstr.services.maf.models.containers.ApiMovieModel;
import nl.lijstr.services.maf.models.containers.ApiShowModel;
import nl.lijstr.services.retrofit.annotations.RetrofitServiceAnnotation;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Stoux on 28/01/2016.
 */
@RetrofitServiceAnnotation(value = RetrofitEndpoints.MY_API_FILMS, readTimeout = 120)
public interface ImdbService {

    /**
     * Get a Movie from the IMDB Api.
     *
     * @param token     The API Token
     * @param imdbId    The IMDB ID
     * @param format    The format (json)
     * @param language  The language (en-us)
     * @param technical Add technical details
     * @param trivia    Add trivia about this movie
     * @param actors    Add the characters
     *
     * @return the ApiMovieModel call
     */
    @GET("/imdb/idIMDB")
    Call<ApiMovieModel> getMovie(@Query("token") String token, @Query("idIMDB") String imdbId,
                                 @Query("format") String format, @Query("language") String language,
                                 @Query("aka") int otherNames, @Query("technical") int technical,
                                 @Query("movieTrivia") int trivia, @Query("actors") int actors);

    /**
     * Get a Show from the IMDB Api.
     *
     * @param token     The API Token
     * @param imdbId    The IMDB ID
     * @param format    The format (json)
     * @param language  The language (en-us)
     * @param seasons   Add seasons
     * @param trivia    Add trivia about this movie
     * @param actors    Add the characters
     *
     * @return the ApiShowModel call
     */
    @GET("/imdb/idIMDB")
    Call<ApiShowModel> getShow(@Query("token") String token, @Query("idIMDB") String imdbId,
                               @Query("format") String format, @Query("language") String language,
                               @Query("aka") int otherNames, @Query("seasons") int seasons,
                               @Query("movieTrivia") int trivia, @Query("actors") int actors);


    @FunctionalInterface
    interface ServiceMethod<X> {

        Call<X> get(String token, String imdbId, String format, String language, int otherNames, int technical,
                        int trivia, int actors);

    }

}
