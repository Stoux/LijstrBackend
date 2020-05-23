package nl.lijstr.services.omdb;

import java.util.List;
import java.util.Map;
import nl.lijstr.common.Utils;
import nl.lijstr.exceptions.BadRequestException;
import nl.lijstr.processors.annotations.InjectRetrofitService;
import nl.lijstr.services.omdb.models.OmdbObject;
import nl.lijstr.services.omdb.models.OmdbSearchContainer;
import nl.lijstr.services.omdb.models.OmdbSearchResultObject;
import nl.lijstr.services.omdb.models.OmdbType;
import nl.lijstr.services.omdb.retrofit.OmdbService;
import org.springframework.beans.factory.annotation.Value;
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

    private final String apiKey;

    public OmdbApiService(@Value("${omdb.api-key}") String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Get a Movie from the OMDB service.
     *
     * @param imdbId The IMDB ID
     *
     * @return the OMDB object
     */
    public OmdbObject getMovie(String imdbId) {
        final OmdbObject omdbObject = get(imdbId);
        if (omdbObject == null || !omdbObject.isMovie()) {
            throw new BadRequestException(imdbId + " is not a movie!");
        }
        return omdbObject;
    }

    /**
     * Get an OMDB Object from the OMDB service.
     *
     * @param imdbId The IMDB ID
     *
     * @return the OMDB object
     */
    public OmdbObject get(String imdbId) {
        final Call<OmdbObject> call = omdbService.getByImdbId(imdbId, apiKey);
        return Utils.executeCall(call);
    }

    /**
     * Search for OMDB results from the OMDB service
     *
     * @param query Search query (title)
     * @param type Type (movie, series, episode)
     *
     * @return list of results
     */
    public List<OmdbSearchResultObject> search(String query, OmdbType type) {
        final Call<OmdbSearchContainer> call = omdbService.search(query, type.name().toLowerCase(), apiKey);
        final OmdbSearchContainer result = Utils.executeCall(call);
        return result.getResults();
    }

}
