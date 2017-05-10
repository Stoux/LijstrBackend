package nl.lijstr.services.tvmaze;

import java.io.IOException;
import java.util.Optional;
import nl.lijstr.common.Utils;
import nl.lijstr.domain.shows.Show;
import nl.lijstr.exceptions.LijstrException;
import nl.lijstr.processors.annotations.InjectLogger;
import nl.lijstr.processors.annotations.InjectRetrofitService;
import nl.lijstr.services.tvmaze.handlers.TvmUpdateHandler;
import nl.lijstr.services.tvmaze.models.TvmShow;
import nl.lijstr.services.tvmaze.retrofit.TvMazeApi;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

/**
 * A Service that provides access to the external api 'TvMaze.com'.
 * API provides data about shows.
 */
@Service
public class TvMazeService {

    @InjectLogger
    private Logger logger;

    @InjectRetrofitService
    private TvMazeApi tvMazeApi;

    @Autowired
    private TvmUpdateHandler updateHandler;

    /**
     * Update a Show using TvMaze data.
     *
     * @param show The show that needs to be updated
     *
     * @return the updated show
     */
    public Show updateShow(Show show) {
        Call<TvmShow> call = tvMazeApi.getShow(show.getTvMazeId());
        TvmShow tvmShow = Utils.executeCall(call);

        logger.info("Updating show: " + show.getImdbId());
        return updateHandler.update(show, tvmShow);
    }

    /**
     * Find the TvMaze ID that matches with an IMDB ID.
     *
     * @param imdbId The IMDB ID
     *
     * @return the id if found
     */
    public Optional<Long> getTvMazeId(String imdbId) {
        Call<TvmShow> call = tvMazeApi.lookupByImdbId(imdbId);
        try {
            Response<TvmShow> response = call.execute();
            if (response.isSuccessful()) {
                return Optional.of(response.body().getId());
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            logger.warn("Failed to call TvMaze API: {}", e.getMessage());
            throw new LijstrException("Failed to contact API");
        }
    }

}
