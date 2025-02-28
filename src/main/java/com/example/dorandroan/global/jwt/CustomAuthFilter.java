package com.example.dorandroan.global.jwt;

import com.example.dorandroan.dto.MemberLoginResponseDto;
import com.example.dorandroan.entity.Member;
import com.example.dorandroan.global.config.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
public class CustomAuthFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    public CustomAuthFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/users/login", "POST"));
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        return authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password, null));
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

        member.publishToken(refreshToken);
        System.out.println(member.getRefreshToken());

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode rootNode = objectMapper.valueToTree(MemberLoginResponseDto.toDto(member, accessToken));

        objectMapper.writeValue(response.getWriter(), rootNode);
    }

}