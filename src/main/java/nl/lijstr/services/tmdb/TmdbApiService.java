package nl.lijstr.services.tmdb;

import com.uwetrottmann.tmdb2.Tmdb;
import com.uwetrottmann.tmdb2.entities.AppendToResponse;
import com.uwetrottmann.tmdb2.entities.FindResults;
import com.uwetrottmann.tmdb2.entities.TvSeason;
import com.uwetrottmann.tmdb2.entities.TvShow;
import com.uwetrottmann.tmdb2.enumerations.AppendToResponseItem;
import com.uwetrottmann.tmdb2.enumerations.ExternalSource;
import nl.lijstr.common.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Service that provides access to the external API 'TheMovieDB.org'.
 */
@Service
public class TmdbApiService {

    private final Tmdb tmdbApi;

    public TmdbApiService(@Value("${tmdb.api-key}") String apiKey) {
        this.tmdbApi = new Tmdb(apiKey);
    }

    /**
     * Fetch an TMDB show by it's ID.
     *
     * @param id The TMDB ID
     * @return The show with external IDs, translations & content ratings
     */
    public TvShow getShow(int id) {
        final Call<TvShow> showCall = tmdbApi.tvService().tv(id, null, new AppendToResponse(
            AppendToResponseItem.EXTERNAL_IDS,
            AppendToResponseItem.TRANSLATIONS,
            AppendToResponseItem.CONTENT_RATINGS,
            AppendToResponseItem.KEYWORDS,
            AppendToResponseItem.CREDITS
        ));
        return Utils.executeCall(showCall);
    }

    /**
     * Get the season by it's show ID & sequential number
     *
     * @param showId The Show's TMDB ID
     * @param seasonSequentialNumber The sequential number of the season (0 = specials, 1, 2, 3, etc.)
     *
     * @return the season with external IDs
     */
    public TvSeason getSeason(int showId, int seasonSequentialNumber) {
        final Call<TvSeason> seasonCall = tmdbApi.tvSeasonsService().season(showId, seasonSequentialNumber, null, new AppendToResponse(
            AppendToResponseItem.EXTERNAL_IDS
        ));
        return Utils.executeCall(seasonCall);
    }


    /**
     * Find a TMDB Movie by an IMDB ID.
     *
     * @param imdbId The IMDB ID
     * @return the ID if found
     */
    public Optional<Integer> findMovieByImdbId(String imdbId) {
        return findIdInResults(imdbId, r -> r.movie_results, i -> i.id);
    }

    /**
     * Find a TMDB Show by an IMDB ID.
     *
     * @param imdbId The IMDB ID
     * @return the ID if found
     */
    public Optional<Integer> findShowByImdbId(String imdbId) {
        return findIdInResults(imdbId, r -> r.tv_results, i -> i.id);
    }

    private <X> Optional<Integer> findIdInResults(String imdbId, Function<FindResults, List<X>> resultPicker, Function<X, Integer> idFetcher) {
        final Call<FindResults> findCall = tmdbApi.findService().find(imdbId, ExternalSource.IMDB_ID, null);
        final FindResults results = Utils.executeCall(findCall);
        final List<X> itemResults = resultPicker.apply(results);
        if (itemResults != null && !itemResults.isEmpty()) {
            final X item = itemResults.get(0);
            return Optional.of(idFetcher.apply(item));
        } else {
            return Optional.empty();
        }
    }


}
