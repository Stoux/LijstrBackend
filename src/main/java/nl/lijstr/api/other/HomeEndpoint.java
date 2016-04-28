package nl.lijstr.api.other;

import nl.lijstr.beans.AppInfoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A RestControllers that handles calls to the main URL(s).
 */
@RestController
@RequestMapping(value = "/", produces = "application/json")
public class HomeEndpoint {

    @Autowired
    private AppInfoBean infoBean;

    /**
     * Get the about details of this API.
     */
    @RequestMapping(value = {"/", "/about"})
    public AppInfoBean about() {
        return infoBean;
    }

}
