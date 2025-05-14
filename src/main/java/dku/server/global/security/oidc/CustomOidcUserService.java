package dku.server.global.security.oidc;

import dku.server.domain.member.domain.Member;
import dku.server.domain.member.repository.MemberRepository;
import dku.server.global.exception.CustomException;
import dku.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        Member member = findOrCreateMember(oidcUser);

        return new CustomOidcUser(oidcUser, member);
    }

    private Member findOrCreateMember(OidcUser oidcUser) {
        String oidcId = oidcUser.getSubject();
        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();

        if (email == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Member member = memberRepository.findByOidcId(oidcId).orElse(null);
        if (member == null) {
            return memberRepository.save(Member.create(oidcId, email, name));
        } else {
            if (!member.getEmail().equals(email)) {
                member.updateEmail(email);
            }
            if (!member.getName().equals(name)) {
                member.updateName(name);
            }
        }

        return member;
    }

}
