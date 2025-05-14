package dku.server.domain.member.service;


import dku.server.domain.member.domain.Member;
import dku.server.domain.member.dto.response.PersonalInfoResponse;
import dku.server.domain.member.repository.MemberRepository;
import dku.server.global.exception.CustomException;
import dku.server.global.exception.ErrorCode;
import dku.server.global.security.oidc.UserInfo;
import dku.server.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public PersonalInfoResponse getPersonalInfo() {
        UserInfo userInfo = MemberUtil.getCurrentUserInfo();
        Member member = memberRepository.findById(userInfo.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return PersonalInfoResponse.from(member);
    }

}
