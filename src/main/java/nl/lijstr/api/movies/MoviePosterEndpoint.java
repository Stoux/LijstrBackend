package nl.lijstr.api.movies;

import nl.lijstr.api.abs.AbsService;
import nl.lijstr.exceptions.LijstrException;
import nl.lijstr.exceptions.db.NotFoundException;
import nl.lijstr.processors.annotations.InjectLogger;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

/**
 * An endpoint that provides the posters of movies.
 */
@RestController
@RequestMapping(value = "/movies/{movieId:\\d+}/poster", produces = MediaType.IMAGE_JPEG_VALUE)
public class MoviePosterEndpoint extends AbsService {

    @InjectLogger
    private Logger logger;

    @Value("${server.image-location}")
    private String imgFolderLocation;

    /**
     * Get the poster of a certain movie.
     * <p>
     * Note: This implementation does not check whether or not a movie actually exists. It just tries to find
     * the image file associated with the movie ID.
     *
     * @param movieId The ID of the movie
     *
     * @return The poster image bytes
     */
    @RequestMapping()
    public byte[] getPoster(@PathVariable() Long movieId) {
        File imageFile = new File(imgFolderLocation + movieId + ".jpg");
        if (imageFile.exists()) {
            try {
                return FileCopyUtils.copyToByteArray(imageFile);
            } catch (IOException e) {
                logger.error(
                        "Failed to return image while it exists for ID: {} | Reason: {}",
                        movieId, e.getMessage(), e
                );
                throw new LijstrException("Failed provide image");
            }
        } else {
            throw new NotFoundException("Movie poster", movieId);
        }
    }

}
