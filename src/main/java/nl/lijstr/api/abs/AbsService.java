package nl.lijstr.api.abs;

import java.util.Map;
import nl.lijstr.common.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * The base AbsService used for {@link org.springframework.web.bind.annotation.RestController}s.
 */
@CrossOrigin
public abstract class AbsService {

    protected ResponseEntity<Map> ok(String message) {
        return new ResponseEntity<>(
                Utils.asMap("message", message),
                HttpStatus.OK
        );
    }

}
