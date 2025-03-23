package com.example.dorandroan.service;

import com.example.dorandroan.dto.MemberLoginResponseDto;
import com.example.dorandroan.dto.MyPageResponseDto;
import com.example.dorandroan.global.CookieUtil;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.MemberErrorCode;
import com.example.dorandroan.global.error.TokenErrorCode;
import com.example.dorandroan.global.jwt.JwtUtil;
import com.example.dorandroan.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RedisService redisService;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    public MemberLoginResponseDto relogin(HttpServletRequest request, HttpServletResponse response) {
        String refresh = cookieUtil.getRefreshFromCookie(request);
        String access = null;
        Long memberId = jwtUtil.getMemberIdFromToken(refresh, "refresh");
        if (jwtUtil.validateRefreshToken(refresh) && redisService.isTokenBlackListed(refresh)) {
            refresh = jwtUtil.createRefreshToken(memberId, "USER");
            access = jwtUtil.createAccessToken(memberId, "USER");
            redisService.saveRefresh(memberId, refresh);
        }
        cookieUtil.setTokenCookies(request, response, access, refresh, false);

        return MemberLoginResponseDto.toDto(memberRepository.findById(memberId)
                .orElseThrow(() -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND)));
    }
    public MyPageResponseDto getMyPage(HttpServletRequest request) {

        return MyPageResponseDto.toDto(memberRepository.findById(jwtUtil.getMemberIdFromAccessToken(request))
                .orElseThrow(() -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND)));
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String access = cookieUtil.getAccessFromCookie(request);
        String refresh = cookieUtil.getRefreshFromCookie(request);
        if (jwtUtil.validateRefreshToken(refresh) && jwtUtil.validateAccessToken(access)){
            redisService.addBlackList(jwtUtil.getMemberIdFromToken(access, "access"), refresh, access);
            cookieUtil.setTokenCookies(request, response, access, refresh, true);
        } else {
            throw new RestApiException(TokenErrorCode.INVALID_ACCESS_TOKEN);
        }
    }
}
