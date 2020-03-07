package nl.lijstr.services.maf;

import nl.lijstr.common.Utils;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.processors.annotations.InjectLogger;
import nl.lijstr.processors.annotations.InjectRetrofitService;
import nl.lijstr.services.maf.handlers.MovieUpdateHandler;
import nl.lijstr.services.maf.models.ApiMovie;
import nl.lijstr.services.maf.models.containers.ApiMovieModel;
import nl.lijstr.services.maf.retrofit.ImdbService;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;

/**
 * A Service that provides access to the external api 'MyApiFilms.com'.
 * An API that provides data from IMDB, TheMovieDB and more.
 */
@Service
public class MafApiService {

    @Value("${maf.token}")
    private String token;

    @InjectLogger
    private Logger logger;

    @InjectRetrofitService
    private ImdbService imdbService;

    @Autowired
    private MovieUpdateHandler updateHandler;

    /**
     * Trigger a movie to be updated by using the latest data from MyApiFilms.
     *
     * @param movie The movie
     *
     * @return The updated movie
     */
    public Movie updateMovie(Movie movie) {
        //Get the most recent data from the API
        ApiMovie apiMovie = getApiMovie(movie.getImdbId());
        logger.info("Updating movie: " + movie.getImdbId());
        return updateHandler.update(movie, apiMovie);
    }

    private ApiMovie getApiMovie(String imdbId) {
        Call<ApiMovieModel> movieCall = imdbService.getMovie(
            token, imdbId, "json", "en-us", 1, 1, 1,
            ImdbService.FULL, ImdbService.FULL, ImdbService.FULL
        );
        ApiMovieModel model = Utils.executeCall(movieCall);
        return model.getMovie();
    }

}
