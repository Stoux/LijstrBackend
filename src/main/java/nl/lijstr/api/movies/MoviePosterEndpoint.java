package nl.lijstr.api.movies;

import nl.lijstr.api.abs.base.TargetPosterEndpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * An endpoint that provides the posters of movies.
 */
@RestController
@RequestMapping(value = "/movies/{id:\\d+}/poster")
public class MoviePosterEndpoint extends TargetPosterEndpoint {

    /**
     * Create a new Poster endpoint.
     *
     * @param imgFolderLocation The location of the posters
     */
    public MoviePosterEndpoint(@Value("${server.image-location.movies}") String imgFolderLocation) {
        super(imgFolderLocation, "Movie");
    }

}
