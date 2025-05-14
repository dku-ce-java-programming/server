package dku.server.global.util;

import dku.server.global.exception.CustomException;
import dku.server.global.exception.ErrorCode;
import dku.server.global.security.oidc.CustomOidcUser;
import dku.server.global.security.oidc.UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class MemberUtil {

    public static UserInfo getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomOidcUser)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return ((CustomOidcUser) authentication.getPrincipal()).getMemberInfo();
    }
}
