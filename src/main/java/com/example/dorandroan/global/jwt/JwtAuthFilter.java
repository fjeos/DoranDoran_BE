package com.example.dorandroan.global.jwt;

import com.example.dorandroan.global.CookieUtil;
import com.example.dorandroan.global.ErrorCode;
import com.example.dorandroan.global.error.TokenErrorCode;
import com.example.dorandroan.service.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final RedisService redisService;
    private final CookieUtil cookieUtil;
    private final JwtUtil jwtUtil;

    private static final String ACCESS_PREFIX = "access";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = cookieUtil.getAccessFromCookie(request);
        if (token != null) {
            try {
                Claims claims = jwtUtil.parseClaims(token, ACCESS_PREFIX);

                if (redisService.isTokenBlackListed(token)) // 블랙리스트 된 토큰이면 ExpiredJwtException 발생
                    throw new ExpiredJwtException(null, claims, null);

                CustomUserDetails userDetails = customUserDetailsService.loadUserById(
                        jwtUtil.getMemberIdFromToken(token, ACCESS_PREFIX));

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);

            } catch (IllegalArgumentException ex) {
                setErrorResponse(response, TokenErrorCode.NULL_TOKEN);

                return;
            }  catch (ExpiredJwtException e) {
                setErrorResponse(response, TokenErrorCode.EXPIRED_TOKEN);

                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void setErrorResponse(HttpServletResponse response, ErrorCode code) throws IOException {
        log.error(code.getMessage());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format("{\"message\": \"%s\"}", code.getMessage()));
    }
}
