package nl.lijstr.services.omdb;

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
        final Call<OmdbObject> call = omdbService.getByImdbId(imdbId);
        final OmdbObject omdbObject = Utils.executeCall(call);
        if (omdbObject == null || !omdbObject.isMovie()) {
            throw new BadRequestException(imdbId + " is not a movie!");
        }
        return omdbObject;
    }

}
