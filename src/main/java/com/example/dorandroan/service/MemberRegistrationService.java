package com.example.dorandroan.service;

import com.example.dorandroan.dto.SignUpRequestDto;
import com.example.dorandroan.entity.Member;
import com.example.dorandroan.entity.Role;
import com.example.dorandroan.global.RestApiException;
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
    private final JwtUtil jwtUtil;

    @Transactional
    public void signUp(SignUpRequestDto requestDto) {

        if (findByNickname(requestDto.getNickname()) || findByEmail(requestDto.getEmail())) {
            throw new RestApiException(MemberErrorCode.DUPLICATED_NICKNAME);
        }

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
    //TODO 로그아웃 로직 변경 -> 쿠키에서 refresh 가져오기
    public void logout(CustomUserDetails user, HttpServletRequest request) {
        Long memberId = null;
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith(("Bearer "))) {
            String token = header.substring(7);
            if (jwtUtil.validateAccessToken(token)) {
                memberId = jwtUtil.getMemberIdFromToken(token, "access");
            } else {
                throw new RestApiException(TokenErrorCode.INVALID_ACCESS_TOKEN);
            }
        }
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND));

    }

    private boolean findByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    public boolean findByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

}
