package com.example.dorandroan.controller;

import com.example.dorandroan.dto.*;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.MemberErrorCode;
import com.example.dorandroan.global.jwt.CustomUserDetails;
import com.example.dorandroan.service.MailService;
import com.example.dorandroan.service.MemberRegistrationService;
import com.example.dorandroan.service.MemberService;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Enumeration;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
public class MemberController {

    private final MemberRegistrationService memberRegistrationService;
    private final MailService mailService;
    private final MemberService memberService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private static String s3Url;
    @PostConstruct
    private void init() {
        s3Url = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/";
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequestDto requestDto) {
        memberRegistrationService.signUp(requestDto);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam("word") String nickname) {
        if(!nickname.matches("^[가-힣a-zA-Z]{2,8}$")) {
            throw new RestApiException(MemberErrorCode.MISMATCH_NICKNAME_FORMAT);
        }
        memberRegistrationService.checkNickname(nickname);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Void> logout(@AuthenticationPrincipal CustomUserDetails user, HttpServletRequest request) {
        memberRegistrationService.logout(user, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/email")
    public ResponseEntity<ClientCodeResponseDto> sendEmail(@Valid @RequestBody EmailAuthRequestDto requestDto) throws MessagingException {
        return ResponseEntity.ok(mailService.sendEmail(requestDto));
    }

    @PostMapping("/auth/code")
    public ResponseEntity<Void> authCode(@RequestBody CodeAuthRequestDto authCode) {
        mailService.confirmCode(authCode);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/relogin")
    public ResponseEntity<MemberLoginResponseDto> relogin(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(memberService.relogin(request, response));
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        memberService.logout(request, response);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reissue")
    public ResponseEntity<Void> reissue(HttpServletRequest request, HttpServletResponse response) {
        memberService.reissue(request, response);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/mypage")
    public ResponseEntity<MyPageResponseDto> myPage(@AuthenticationPrincipal CustomUserDetails member) {

        return ResponseEntity.ok(memberService.getMyPage(member.getMember()));
    }

    @PatchMapping("/mypage/nickname")
    public ResponseEntity<Void> changeNickname(@AuthenticationPrincipal CustomUserDetails member,
                                               @RequestBody Map<String, Object> dto) {
        String nickname = (String)dto.get("nickname");
        if (!nickname.matches("^[가-힣a-zA-Z]{2,8}$"))
            throw new RestApiException(MemberErrorCode.MISMATCH_NICKNAME_FORMAT);
        memberService.changeNickname(member.getMember().getMemberId(), nickname);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/mypage/profile")
    public ResponseEntity<Void> changeProfile(@AuthenticationPrincipal CustomUserDetails member,
                                              @RequestBody Map<String, String> img) {
        String profileImage = img.get("profileImage");
        if (!profileImage.startsWith(s3Url)) {
            throw new RestApiException(MemberErrorCode.MISMATCH_IMG_URL_FORMAT);
        }
        memberService.changeProfileImg(member.getMember().getMemberId(), profileImage);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/mypage/recommends")
    public ResponseEntity<Void> toggleRecommends(@AuthenticationPrincipal CustomUserDetails member) {
        memberService.toggleRecommends(member.getMember().getMemberId());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/mypage/notification")
    public ResponseEntity<Void> togglePush(@AuthenticationPrincipal CustomUserDetails member) {
        memberService.togglePush(member.getMember().getMemberId());
        return ResponseEntity.ok().build();
    }
}
