package com.example.dorandroan.controller;

import com.example.dorandroan.dto.CodeAuthRequestDto;
import com.example.dorandroan.dto.ClientCodeResponseDto;
import com.example.dorandroan.dto.EmailAuthRequestDto;
import com.example.dorandroan.dto.SignUpRequestDto;
import com.example.dorandroan.global.jwt.CustomUserDetails;
import com.example.dorandroan.service.MailService;
import com.example.dorandroan.service.MemberRegistrationService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Enumeration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
public class MemberController {

    private final MemberRegistrationService memberRegistrationService;
    private final MailService mailService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequestDto requestDto) {
        memberRegistrationService.signUp(requestDto);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam("word") String nickname) {
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

    @PostMapping("/mypage")
    public ResponseEntity<Void> myPage() {
        log.info("mypage");
        return ResponseEntity.ok().build();
    }
}
