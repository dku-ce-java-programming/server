package dku.server.domain.member.dto.response;

import dku.server.domain.member.domain.Member;

public record PersonalInfoResponse(
        Long memberId,
        String email,
        String name
) {
    public static PersonalInfoResponse from(Member member) {
        return new PersonalInfoResponse(
                member.getId(),
                member.getEmail(),
                member.getName()
        );
    }
}
