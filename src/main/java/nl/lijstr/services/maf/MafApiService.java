package nl.lijstr.services.maf;

import nl.lijstr.common.Utils;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.shows.Show;
import nl.lijstr.processors.annotations.InjectLogger;
import nl.lijstr.processors.annotations.InjectRetrofitService;
import nl.lijstr.services.maf.handlers.MovieUpdateHandler;
import nl.lijstr.services.maf.handlers.ShowUpdateHandler;
import nl.lijstr.services.maf.models.ApiMovie;
import nl.lijstr.services.maf.models.ApiShow;
import nl.lijstr.services.maf.models.containers.ApiMovieModel;
import nl.lijstr.services.maf.models.containers.ApiShowModel;
import nl.lijstr.services.maf.retrofit.ImdbApi;
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
    private ImdbApi imdbService;

    @Autowired
    private MovieUpdateHandler movieUpdateHandler;

    @Autowired
    private ShowUpdateHandler showUpdateHandler;

    /**
     * Trigger a movie to be updated by using the latest data from MyApiFilms.
     *
     * @param movie The movie
     *
     * @return The updated movie
     */
    public Movie updateMovie(Movie movie) {
        //Get the most recent data from the API
        ApiMovieModel model = get(movie.getImdbId(), imdbService::getMovie);
        ApiMovie apiMovie = model.getMovie();
        if (apiMovie == null) {
            logger.info("Failed to fetch movie from MAF.");
            return null;
        }

        logger.info("Updating movie: " + movie.getImdbId());
        return movieUpdateHandler.update(movie, apiMovie);
    }

    /**
     * Trigger a show to be updated by using the latest data from MyApiFilms.
     *
     * @param show The show
     *
     * @return The updated show
     */
    public Show updateShow(Show show) {
        ApiShowModel model = get(show.getImdbId(), imdbService::getShow);
        ApiShow apiShow = model.getShow();
        if (apiShow == null) {
            logger.info("Failed to fetch movie from MAF.");
            return null;
        }

        logger.info("Updating show: " + show.getImdbId());
        return showUpdateHandler.update(show, apiShow);
    }

    @SuppressWarnings("unchecked")
    private <X> X get(String imdbId, ImdbApi.ServiceMethod method) {
        Call<X> call = method.get(token, imdbId, "json", "en-us", 1, 1, 1, 1);
        return Utils.executeCall(call);
    }

}
