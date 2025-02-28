package com.example.dorandroan.service;

import com.example.dorandroan.dto.SignUpRequestDto;
import com.example.dorandroan.entity.Member;
import com.example.dorandroan.entity.Role;
import com.example.dorandroan.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(SignUpRequestDto requestDto) {

        if (findByNickname(requestDto.getNickname()) || findByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("Already Exists");
        }

        Member member = memberRepository.save(Member.builder().email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .nickname(requestDto.getNickname())
                .profileImg(null)
                .state(true)
                .recommends(true)
                .refreshToken(null)
                .deviceToken(null)
                .role(Role.USER)
                .build());

    }

    private boolean findByNickname(String nickname) {
        return memberRepository.findByNickname(nickname).isPresent();
    }

    private boolean findByEmail(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }
}
