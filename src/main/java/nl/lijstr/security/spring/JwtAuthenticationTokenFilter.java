package nl.lijstr.security.spring;

import nl.lijstr.security.model.JwtUser;
import nl.lijstr.security.util.JwtTokenHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A {@link UsernamePasswordAuthenticationFilter} that intercepts authentication requests.
 * It allows for the possibility to login using JSON Web Tokens.
 */
@Component
public class JwtAuthenticationTokenFilter extends UsernamePasswordAuthenticationFilter {

    private static final String TOKEN_HEADER = "Authorization";

    @Autowired
    private JwtTokenHandler jwtTokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @SuppressWarnings("squid:UnusedPrivateMethod")
    @PostConstruct
    private void setManager() {
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        //Find the token
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authToken = httpRequest.getHeader(TOKEN_HEADER);

        //Do not login if no token available or if already logged in
        if (authToken == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        //Try to login
        JwtUser user;
        try {
            user = jwtTokenUtil.parseToken(authToken);
        } catch (AuthenticationException e) {
            unsuccessfulAuthentication(httpRequest, (HttpServletResponse) response, e);
            return;
        }

        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(user);
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        //Finish
        chain.doFilter(request, response);
    }

}
