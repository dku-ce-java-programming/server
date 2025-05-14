package dku.server.global.security.oidc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final HttpSession httpSession;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        CustomOidcUser customOidcUser = (CustomOidcUser) authentication.getPrincipal();
        UserInfo userInfo = customOidcUser.getMemberInfo();

        httpSession.setAttribute("userInfo", userInfo);

        String redirectUri = (String) httpSession.getAttribute("redirectUri");
        httpSession.removeAttribute("redirectUri");

        response.sendRedirect(redirectUri);
    }
}
