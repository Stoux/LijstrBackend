package nl.lijstr.api.other;

import nl.lijstr.api.abs.AbsService;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.exceptions.BadRequestException;
import nl.lijstr.processors.annotations.InjectLogger;
import nl.lijstr.services.omdb.OmdbApiService;
import nl.lijstr.services.omdb.models.OmdbObject;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint for proxying the OMDB endpoint.
 */
@RestController
@RequestMapping(value = "/omdb", produces = "application/json")
public class OmdbProxyEndpoint extends AbsService {

    @InjectLogger
    private Logger logger;

    private final OmdbApiService apiService;

    @Autowired
    public OmdbProxyEndpoint(OmdbApiService apiService) {
        this.apiService = apiService;
    }

    /**
     * Proxies the OMDB endpoint to be accessiable from frontend.
     * <p>
     * This way the API key isn't exposed to the frontend.
     *
     * @param imdbId The IMDB ID
     *
     * @return The OmdbObject
     */
    @Secured({Permission.MOVIE_MOD, Permission.ADMIN})
    @RequestMapping("/{imdbId:tt\\d{7,8}}")
    public OmdbObject proxy(@PathVariable("imdbId") String imdbId) {
        OmdbObject omdbObject = apiService.get(imdbId);
        if (!omdbObject.isSuccessful()) {
            throw new BadRequestException(omdbObject.getError());
        }
        return omdbObject;
    }

}
