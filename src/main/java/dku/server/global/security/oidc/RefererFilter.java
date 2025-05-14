package dku.server.global.security.oidc;

import dku.server.global.exception.CustomException;
import dku.server.global.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static dku.server.global.constant.Constant.ALLOWED_CLIENT_URLS;

@Component
public class RefererFilter extends OncePerRequestFilter {

    private final RequestMatcher authorizationRequestMatcher = new AntPathRequestMatcher("/oauth2/authorization/**");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        if (authorizationRequestMatcher.matches(request)) {
            String referer = request.getHeader("Referer");
            if (referer != null && !referer.isEmpty()) {
                HttpSession session = request.getSession();
                if (validateReferer(referer)) {
                    session.setAttribute("redirectUri", referer);
                } else {
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean validateReferer(String referer) {
        String host = extractHost(referer);
        return ALLOWED_CLIENT_URLS.stream().anyMatch(allowedUrl -> isAllowedHost(allowedUrl, host));
    }

    private boolean isAllowedHost(String allowedUrl, String host) {
        return extractHost(allowedUrl).equals(host);
    }

    private String extractHost(String uriString) {
        try {
            return new URI(uriString).getHost();
        } catch (URISyntaxException e) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }
}
