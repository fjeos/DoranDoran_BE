package com.example.dorandroan.service;

import com.example.dorandroan.entity.AuthCode;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.MailAuthErrorCode;
import com.example.dorandroan.repository.AuthCodeRepository;
import com.example.dorandroan.repository.BlockedTokenRepository;
import com.example.dorandroan.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RefreshRepository refreshRepository;
    private final BlockedTokenRepository blockedRepository;
    private final AuthCodeRepository authCodeRepository;


    public void saveCode(String receiverEmail, Integer authCode) {
        authCodeRepository.save(AuthCode.builder().email(receiverEmail).code(authCode).build());
    }

    public AuthCode findEmailAndCode(Map<String, Integer> authCode) {
        return authCodeRepository.findById(authCode.get("authCode"))
                .orElseThrow(() -> new RestApiException(MailAuthErrorCode.CANNOT_FOUND_CODE));

    }
}
