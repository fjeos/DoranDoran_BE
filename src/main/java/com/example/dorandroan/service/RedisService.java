package com.example.dorandroan.service;

import com.example.dorandroan.entity.AuthCode;
import com.example.dorandroan.entity.BlockedToken;
import com.example.dorandroan.entity.RefreshToken;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.CommonErrorCode;
import com.example.dorandroan.global.error.MailAuthErrorCode;
import com.example.dorandroan.global.error.MemberErrorCode;
import com.example.dorandroan.repository.AuthCodeRepository;
import com.example.dorandroan.repository.BlockedTokenRepository;
import com.example.dorandroan.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RefreshRepository refreshRepository;
    private final BlockedTokenRepository blockedRepository;
    private final AuthCodeRepository authCodeRepository;


    public void saveCode(String clientCode, Integer authCode) {
        authCodeRepository.save(AuthCode.builder().clientCode(clientCode).authCode(authCode).build());
    }

    public AuthCode findByClientCode(String clientCode) {
        return authCodeRepository.findByClientCode(clientCode)
                .orElseThrow(() -> new RestApiException(MailAuthErrorCode.CANNOT_FOUND_CLIENT));
    }

    public boolean isTokenBlackListed(String token) {
        return blockedRepository.findByToken(token) != null;
    }

    public void saveRefresh(Long memberId, String refreshToken) {
        refreshRepository.save(RefreshToken.builder().memberId(memberId).refresh(refreshToken).build());
    }
}
