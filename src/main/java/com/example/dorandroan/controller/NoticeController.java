package com.example.dorandroan.controller;

import com.example.dorandroan.global.jwt.CustomUserDetails;
import com.example.dorandroan.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

    private final MemberService memberService;

    @PostMapping("/device-token")
    public ResponseEntity<Void> saveDeviceToken(@AuthenticationPrincipal CustomUserDetails member,
                                                @RequestBody Map<String, String> requestDto) {
        memberService.saveDeviceToken(member.getMember(), requestDto.get("deviceToken"));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/device-token")
    public ResponseEntity<Void> deleteDeviceToken(@AuthenticationPrincipal CustomUserDetails member) {
        memberService.deleteDeviceToken(member.getMember());
        return ResponseEntity.ok().build();
    }
}
