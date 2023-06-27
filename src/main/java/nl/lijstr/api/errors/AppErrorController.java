package nl.lijstr.api.errors;

import nl.lijstr.processors.annotations.InjectLogger;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Map;

/**
 * Created by Stoux on 27/01/2016.
 */
@RestController
@RequestMapping(value = AppErrorController.ERROR_PATH, produces = "application/json")
public class AppErrorController implements ErrorController {

    public static final String ERROR_PATH = "/error";

    @InjectLogger
    private Logger logger;

    @Autowired
    private ErrorAttributes errorAttributes;

    /**
     * Serve an Error Page.
     *
     * @param request The request
     *
     * @return The response
     */
    @RequestMapping
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Map<String, Object> body = getErrorAttributes(request, getTraceParameter(request));
        HttpStatus status = getStatus(request);
        return new ResponseEntity<>(body, status);
    }

    private boolean getTraceParameter(HttpServletRequest request) {
        String parameter = request.getParameter("trace");
        return parameter != null && !"false".equalsIgnoreCase(parameter);
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
        ServletWebRequest requestAttributes = new ServletWebRequest(request);
        return this.errorAttributes.getErrorAttributes(
                requestAttributes,
                ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STACK_TRACE)
        );
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode != null) {
            try {
                return HttpStatus.valueOf(statusCode);
            } catch (IllegalArgumentException ex) {
                //A thing happened.
                logger.warn(ex);
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
