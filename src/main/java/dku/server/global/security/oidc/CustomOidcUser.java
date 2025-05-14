package dku.server.global.security.oidc;

import dku.server.domain.member.domain.Member;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collections;

@Getter
public class CustomOidcUser extends DefaultOidcUser {

    private final UserInfo memberInfo;

    public CustomOidcUser(OidcUser oidcUser, Member member) {
        super(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oidcUser.getIdToken(),
                oidcUser.getUserInfo()
        );
        this.memberInfo = UserInfo.from(member);
    }
}
