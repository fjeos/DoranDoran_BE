package com.example.dorandroan.service;

import com.example.dorandroan.dto.ResetPwRequestDto;
import com.example.dorandroan.dto.SignUpRequestDto;
import com.example.dorandroan.entity.Member;
import com.example.dorandroan.entity.Role;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.MailAuthErrorCode;
import com.example.dorandroan.global.error.MemberErrorCode;
import com.example.dorandroan.global.error.TokenErrorCode;
import com.example.dorandroan.global.jwt.CustomUserDetails;
import com.example.dorandroan.global.jwt.JwtUtil;
import com.example.dorandroan.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberRegistrationService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;

    @Transactional
    public void signUp(SignUpRequestDto requestDto) {

        if (findByNickname(requestDto.getNickname()) || findByEmail(requestDto.getEmail()))
            throw new RestApiException(MemberErrorCode.DUPLICATED_NICKNAME);
        if (redisService.isConfirmedEmail(requestDto.getEmail()))
            throw new RestApiException(MemberErrorCode.UNAPPROVED_EMAIL);

        memberRepository.save(Member.builder().email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .nickname(requestDto.getNickname())
                .profileImg(null)
                .state(true)
                .recommends(true)
                .deviceToken(null)
                .role(Role.USER)
                .build());

    }

    public void checkNickname(String nickname) {
        if (this.findByNickname(nickname)) {
            throw new RestApiException(MemberErrorCode.DUPLICATED_NICKNAME);
        }
    }

    private boolean findByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    public boolean findByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Transactional
    public void resetPassword(ResetPwRequestDto requestDto) {
        String email = requestDto.getEmail();
        if (!redisService.isConfirmedEmail(email)) {
            throw new RestApiException(MemberErrorCode.UNAPPROVED_EMAIL);
        }
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND));
        String newPassword = requestDto.getNewPassword();
        if (passwordEncoder.matches(newPassword, member.getPassword()))
            throw new RestApiException(MemberErrorCode.SAME_PASSWORD);
        member.changePassword(passwordEncoder.encode(newPassword));
    }
}
