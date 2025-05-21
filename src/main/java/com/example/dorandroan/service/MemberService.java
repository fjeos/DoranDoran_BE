package com.example.dorandroan.service;

import com.example.dorandroan.dto.MemberLoginResponseDto;
import com.example.dorandroan.dto.MyPageResponseDto;
import com.example.dorandroan.dto.ResetPwRequestDto;
import com.example.dorandroan.entity.Member;
import com.example.dorandroan.global.CookieUtil;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.MailAuthErrorCode;
import com.example.dorandroan.global.error.MemberErrorCode;
import com.example.dorandroan.global.error.TokenErrorCode;
import com.example.dorandroan.global.jwt.JwtUtil;
import com.example.dorandroan.repository.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final RedisService redisService;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @Transactional
    public MemberLoginResponseDto relogin(HttpServletRequest request, HttpServletResponse response) {
        String refresh = cookieUtil.getRefreshFromCookie(request);
        String access = null;
        Long memberId = jwtUtil.getMemberIdFromToken(refresh, "refresh");
        if (jwtUtil.validateRefreshToken(refresh) && !redisService.isTokenBlackListed(refresh)) {
            refresh = jwtUtil.createRefreshToken(memberId, "USER");
            access = jwtUtil.createAccessToken(memberId, "USER");
            redisService.saveRefresh(memberId, refresh);
        }
        cookieUtil.setTokenCookies(request, response, access, refresh, false);

        return MemberLoginResponseDto.toDto(memberRepository.findById(memberId)
                .orElseThrow(() -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND)));
    }
    public MyPageResponseDto getMyPage(Member member) {

        return MyPageResponseDto.toDto(member);
    }

    @Transactional
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

    @Transactional
    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = cookieUtil.getRefreshFromCookie(request);
        Long memberId = jwtUtil.getMemberIdFromToken(refresh, "refresh");

        if (jwtUtil.validateRefreshToken(refresh) && memberId.equals(redisService.findByRefreshToken(refresh))) {
            cookieUtil.setTokenCookies(request, response, jwtUtil.createAccessToken(memberId, "USER"), refresh, false);
        } else {
            throw new RestApiException(MemberErrorCode.UNMATCHED_TOKEN);
        }
    }

    @Transactional
    public void changeNickname(Long memberId, String nickname) {
        Member member = findMember(memberId);
        if (memberRepository.existsByNickname(nickname)) {
            throw new RestApiException(MemberErrorCode.DUPLICATED_NICKNAME);
        }
        member.changeNickname(nickname);
    }

    @Transactional
    public void changeProfileImg(Long memberId, String url) {
        findMember(memberId).changeProfile(url);
    }

    @Transactional
    public void toggleRecommends(Long memberId) {
        findMember(memberId).toggleRecommends();
    }

    @Transactional
    public void togglePush(Long memberId) {
        findMember(memberId). togglePush();
    }

    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public List<Member> getRecommendMembers() {
        return memberRepository.findByRecommends();
    }

    public long countRecommendMembers() {

        return memberRepository.countRecommendMembers();
    }

    public List<Member> findRecommendedMembers() {

        return memberRepository.findRecommendedMembers();
    }
}
