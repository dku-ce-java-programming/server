package dku.server.domain.member.controller;

import dku.server.domain.member.dto.response.PersonalInfoResponse;
import dku.server.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<PersonalInfoResponse> getPersonalInfo() {
        PersonalInfoResponse response = memberService.getPersonalInfo();
        return ResponseEntity.ok(response);
    }
}
