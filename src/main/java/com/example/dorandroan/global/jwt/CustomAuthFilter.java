package com.example.dorandroan.global.jwt;

import com.example.dorandroan.dto.MemberLoginRequestDto;
import com.example.dorandroan.dto.MemberLoginResponseDto;
import com.example.dorandroan.entity.Member;
import com.example.dorandroan.global.CookieUtil;
import com.example.dorandroan.repository.MemberRepository;
import com.example.dorandroan.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;

@Slf4j
public class CustomAuthFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    public CustomAuthFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, RedisService redisService, CookieUtil cookieUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.redisService = redisService;
        this.cookieUtil = cookieUtil;
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/member/login", "POST"));
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            MemberLoginRequestDto requestDto = new ObjectMapper().readValue(
                    request.getInputStream(),
                    MemberLoginRequestDto.class
            );

            return authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword(), null));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication) throws IOException {
        log.info("로그인 성공");
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = userDetails.getMember();

        String accessToken = jwtUtil.createAccessToken(member.getMemberId(), member.getRole().toString());
        String refreshToken = jwtUtil.createRefreshToken(member.getMemberId(), member.getRole().toString());
        redisService.saveRefresh(member.getMemberId(), refreshToken);
        cookieUtil.setTokenCookies(request, response, accessToken, refreshToken);

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode rootNode = objectMapper.valueToTree(MemberLoginResponseDto.toDto(member));
        objectMapper.writeValue(response.getWriter(), rootNode);
    }

}