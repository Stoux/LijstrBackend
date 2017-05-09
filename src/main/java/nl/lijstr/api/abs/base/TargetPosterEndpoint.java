package nl.lijstr.api.abs.base;

import java.io.File;
import java.io.IOException;
import nl.lijstr.api.abs.AbsService;
import nl.lijstr.exceptions.LijstrException;
import nl.lijstr.exceptions.db.NotFoundException;
import nl.lijstr.processors.annotations.InjectLogger;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * An endpoint that provides an endpoint for fetching posters.
 */
public abstract class TargetPosterEndpoint extends AbsService {

    @InjectLogger
    protected Logger logger;

    private String imgFolderLocation;
    private String type;

    protected TargetPosterEndpoint(String imgFolderLocation, String type) {
        this.imgFolderLocation = imgFolderLocation;
        this.type = type;
    }

    /**
     * Get the poster of a certain target.
     * <p>
     * Note: This implementation does not check whether or not the target actually exists. It just tries to find
     * the image file associated with the ID.
     *
     * @param id The ID of the target
     *
     * @return the image as byte array
     */
    @RequestMapping(produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getPoster(@PathVariable() Long id) {
        File imageFile = new File(imgFolderLocation + id + ".jpg");
        if (imageFile.exists()) {
            try {
                return FileCopyUtils.copyToByteArray(imageFile);
            } catch (IOException e) {
                logger.error("Failed to return image while it exists for ID: {} | Reason: {}", id, e.getMessage(), e);
                throw new LijstrException("Failed to load image");
            }
        } else {
            throw new NotFoundException(type + " poster", id);
        }
    }

}
