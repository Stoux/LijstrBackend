package nl.lijstr.api.shows;

import nl.lijstr.api.abs.base.TargetPosterEndpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * An endpoint that provides the posters of shows.
 */
@RestController
@RequestMapping(value = "/shows/{id:\\d+}/poster")
public class ShowPosterEndpoint extends TargetPosterEndpoint {

    /**
     * Create a new Poster endpoint.
     *
     * @param imgFolderLocation The location of the posters
     */
    public ShowPosterEndpoint(@Value("${server.image-location.shows}") String imgFolderLocation) {
        super(imgFolderLocation, "Show");
    }

}
