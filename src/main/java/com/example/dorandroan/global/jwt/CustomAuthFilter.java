package com.example.dorandroan.global.jwt;

import com.example.dorandroan.dto.MemberLoginRequestDto;
import com.example.dorandroan.dto.MemberLoginResponseDto;
import com.example.dorandroan.entity.Member;
import com.example.dorandroan.global.CookieUtil;
import com.example.dorandroan.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
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
import java.util.Enumeration;

@Slf4j
public class CustomAuthFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    public CustomAuthFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, MemberRepository memberRepository, CookieUtil cookieUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
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

        System.out.println("I will Call the print Method");
        printHeaders(request);
        System.out.println("Calling End. I will Call the Cookie Setting method");
        cookieUtil.setTokenCookies(request, response, accessToken, refreshToken);

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode rootNode = objectMapper.valueToTree(MemberLoginResponseDto.toDto(member));
        objectMapper.writeValue(response.getWriter(), rootNode);
    }

    private void printHeaders(HttpServletRequest request){
        System.out.println("In the Print Method First");
        System.out.println("Request Method: " + request.getMethod());

        // 2. 요청 URI
        System.out.println("Request URI: " + request.getRequestURI());

        // 3. 요청 URL
        System.out.println("Request URL: " + request.getRequestURL());

        // 4. 쿼리 파라미터 출력
        System.out.println("Query Parameters: ");
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            for (String value : paramValues) {
                System.out.println(paramName + " = " + value);
            }
        }

        // 5. 요청 헤더 출력
        System.out.println("Request Headers: ");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            System.out.println(headerName + " = " + headerValue);
        }

        // 6. 요청 속성 출력
        System.out.println("Request Attributes: ");
        Enumeration<String> attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            Object attributeValue = request.getAttribute(attributeName);
            System.out.println(attributeName + " = " + attributeValue);
        }

        // 7. 쿠키 출력
        System.out.println("Cookies: ");
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                System.out.println("Cookie: " + cookie.getName() + " = " + cookie.getValue());
            }
        }

    }

}