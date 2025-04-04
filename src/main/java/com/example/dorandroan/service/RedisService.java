package com.example.dorandroan.service;

import com.example.dorandroan.entity.AuthCode;
import com.example.dorandroan.entity.BlockedToken;
import com.example.dorandroan.entity.RefreshToken;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.MailAuthErrorCode;
import com.example.dorandroan.global.error.MemberErrorCode;
import com.example.dorandroan.repository.AuthCodeRepository;
import com.example.dorandroan.repository.BlockedTokenRepository;
import com.example.dorandroan.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RedisService {
    private final RefreshRepository refreshRepository;
    private final BlockedTokenRepository blockedRepository;
    private final AuthCodeRepository authCodeRepository;

    /* AuthCode Repository */
    @Transactional
    public void saveCode(String receiverMail, Integer authCode) {
        authCodeRepository.save(AuthCode.builder().email(receiverMail).authCode(authCode).build());
    }

    public AuthCode findByEmail(String email) {
        return authCodeRepository.findById(email)
                .orElseThrow(() -> new RestApiException(MailAuthErrorCode.CANNOT_FOUND_EMAIL));
    }

    public boolean isConfirmedEmail(String email) {
        return authCodeRepository.findById(email)
                .orElseThrow(() -> new RestApiException(MailAuthErrorCode.CANNOT_FOUND_EMAIL)).getApproved();
    }

    @Transactional
    public void confirmCode(AuthCode foundCode) {
        foundCode.updateApproved();
        authCodeRepository.save(foundCode);
    }

    /* Blocked Token Repository */
    public boolean isTokenBlackListed(String token) {
        return blockedRepository.findByToken(token) != null;
    }

    @Transactional
    public void addBlackList(Long memberId, String refresh, String access) {

        blockedRepository.save(BlockedToken.builder().memberId(memberId)
                .token(refresh).build());
        blockedRepository.save(BlockedToken.builder().memberId(memberId)
                .token(access).build());
    }

    /* Refresh Token Repository */
    @Transactional
    public void saveRefresh(Long memberId, String refreshToken) {
        refreshRepository.save(RefreshToken.builder().memberId(memberId).refresh(refreshToken).build());
    }

    public Long findByRefreshToken(String refresh) {
        return refreshRepository.findByRefresh(refresh)
                .orElseThrow(() -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND)).getMemberId();
    }

}
