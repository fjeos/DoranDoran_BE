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
    public ResponseEntity<Void> myPage(HttpServletRequest request) {
// 1. 요청 메서드 (GET, POST, 등)
        System.out.println("Request Method: " + request.getMethod());

        // 2. 요청 URI
        System.out.println("Request URI: " + request.getRequestURI());

        // 3. 요청 URL
        System.out.println("Request URL: " + request.getRequestURL());

        // 4. 쿼리 파라미터 출력
        System.out.println("Query Parameters: ");
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            for (String value : paramValues) {
                System.out.println(paramName + " = " + value);
            }
        }

        // 5. 요청 헤더 출력
        System.out.println("Request Headers: ");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            System.out.println(headerName + " = " + headerValue);
        }

        // 6. 요청 속성 출력
        System.out.println("Request Attributes: ");
        Enumeration<String> attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            Object attributeValue = request.getAttribute(attributeName);
            System.out.println(attributeName + " = " + attributeValue);
        }

        // 7. 쿠키 출력
        System.out.println("Cookies: ");
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                System.out.println("Cookie: " + cookie.getName() + " = " + cookie.getValue());
            }
        }

        // 8. 요청의 사용자 정보 (Principal, 인증된 사용자)
        System.out.println("User Principal: " + request.getUserPrincipal());
        return ResponseEntity.ok().build();
    }
}
