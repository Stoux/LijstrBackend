package nl.lijstr.services.omdb;

import java.util.function.Function;
import nl.lijstr.common.Utils;
import nl.lijstr.exceptions.BadRequestException;
import nl.lijstr.processors.annotations.InjectRetrofitService;
import nl.lijstr.services.omdb.models.OmdbObject;
import nl.lijstr.services.omdb.retrofit.OmdbService;
import org.springframework.stereotype.Service;
import retrofit2.Call;

/**
 * A Service that provides access to the external api 'omdbapi.com'.
 * An API that provides IMDB data.
 */
@Service
public class OmdbApiService {

    @InjectRetrofitService
    private OmdbService omdbService;

    /**
     * Get a Movie from the OMDB service.
     *
     * @param imdbId The IMDB ID
     *
     * @return the OMDB object
     */
    public OmdbObject getMovie(String imdbId) {
        return fetch(imdbId, "movie", OmdbObject::isMovie);
    }

    /**
     * Get a Show from the OMDB service.
     *
     * @param imdbId The IMDB ID
     *
     * @return the OMDB object
     */
    public OmdbObject getShow(String imdbId) {
        return fetch(imdbId, "show", OmdbObject::isSeries);
    }

    /**
     * Get a Show's episode from the OMDB service.
     *
     * @param imdbId The IMDB ID
     *
     * @return the OMDB object
     */
    public OmdbObject getShowEpisode(String imdbId) {
        return fetch(imdbId, "episode", OmdbObject::isSeriesEpisode);
    }

    private OmdbObject fetch(String imdbId, String type, Function<OmdbObject, Boolean> typeCheck) {
        final Call<OmdbObject> call = omdbService.getByImdbId(imdbId);
        final OmdbObject omdbObject = Utils.executeCall(call);
        if (omdbObject == null || !typeCheck.apply(omdbObject)) {
            throw new BadRequestException(imdbId + " is not a " + type + "!");
        }
        return omdbObject;
    }


}
