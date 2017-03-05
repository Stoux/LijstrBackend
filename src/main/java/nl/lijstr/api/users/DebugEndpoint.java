package nl.lijstr.api.users;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import nl.lijstr.api.abs.AbsService;
import nl.lijstr.common.Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Users Endpoint.
 */
@RestController
@RequestMapping(value = "/debug", produces = "application/json")
public class DebugEndpoint extends AbsService {

    @RequestMapping()
    public Map<Object, Object> springRequest(HttpServletRequest springRequest) {
        HashMap<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = springRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.put(name, springRequest.getHeader(name));
        }


        return Utils.asMap(
                1, springRequest.getRemoteUser(),
                2, springRequest.getRemoteAddr(),
                3, springRequest.getRemoteHost(),
                16, springRequest.getRemotePort(),
                4, springRequest.getAuthType(),
                5, springRequest.getContextPath(),
                6, springRequest.getMethod(),
                7, headers,
                8, springRequest.getPathInfo(),
                9, springRequest.getPathTranslated(),
                10, springRequest.getQueryString(),
                11, springRequest.getRequestURI(),
                12, springRequest.getRequestURL(),
                13, springRequest.getLocalAddr(),
                14, springRequest.getServerName(),
                15, springRequest.getServerPort()
        );
    }

}
