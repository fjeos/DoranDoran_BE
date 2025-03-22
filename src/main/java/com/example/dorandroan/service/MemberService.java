package com.example.dorandroan.service;

import com.example.dorandroan.dto.MyPageResponseDto;
import com.example.dorandroan.entity.Member;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.MemberErrorCode;
import com.example.dorandroan.global.jwt.JwtUtil;
import com.example.dorandroan.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public MyPageResponseDto getMyPage(HttpServletRequest request) {

        return MyPageResponseDto.toDto(memberRepository.findById(jwtUtil.getMemberIdFromJwt(request))
                .orElseThrow(() -> new RestApiException(MemberErrorCode.USER_NOT_FOUND)));
    }
}
