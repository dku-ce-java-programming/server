package dku.server.global.security.oidc;

import dku.server.domain.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class UserInfo implements Serializable {

    private Long memberId;
    private String oidcId;
    private String name;
    private String email;
    private String role;

    public static UserInfo from(Member member) {
        return UserInfo.builder()
                .memberId(member.getId())
                .oidcId(member.getOidcId())
                .name(member.getName())
                .email(member.getEmail())
                .build();
    }
}
