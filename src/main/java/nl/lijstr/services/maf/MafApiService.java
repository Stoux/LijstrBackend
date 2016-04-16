package nl.lijstr.services.maf;

import java.io.IOException;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.exceptions.LijstrException;
import nl.lijstr.processors.annotations.InjectLogger;
import nl.lijstr.processors.annotations.InjectRetrofitService;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.services.maf.handlers.MovieUpdateHandler;
import nl.lijstr.services.maf.models.ApiMovie;
import nl.lijstr.services.maf.models.containers.ApiMovieModel;
import nl.lijstr.services.maf.retrofit.ImdbService;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

/**
 * A Service that provides access to the external api 'MyApiFilms.com'.
 */
@Service
public class MafApiService {

    //TODO: Move this to the properties
    public static final String TOKEN = "MOVE-TO-PROPERTIES";

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
        Call<ApiMovieModel> movieCall = imdbService.getMovie(TOKEN, imdbId, "json", "en-us", 1, 1, 1);
        ApiMovieModel model = executeCall(movieCall);
        return model.getMovie();
    }

    private <X> X executeCall(Call<X> call) {
        try {
            Response<X> response = call.execute();
            return response.body();
        } catch (IOException e) {
            throw new LijstrException("Failed to execute call.", e);
        }
    }


}
