package com.example.dorandroan.global.jwt;

import com.example.dorandroan.global.CookieUtil;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.CommonErrorCode;
import com.example.dorandroan.global.error.TokenErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.sql.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtProperties jwtProperties;
    private Key accessKey;
    private Key refreshKey;
    private long accessExpTime;
    private long refreshExpTime;
    private final CookieUtil cookieUtil;

    @PostConstruct
    private void initKeys() {
        byte[] accessBytes = Decoders.BASE64.decode(jwtProperties.getAccess().getSecret());
        byte[] refreshBytes = Decoders.BASE64.decode(jwtProperties.getRefresh().getSecret());
        this.accessKey = Keys.hmacShaKeyFor(accessBytes);
        this.refreshKey = Keys.hmacShaKeyFor(refreshBytes);

        this.accessExpTime = jwtProperties.getAccess().getExpiration();
        this.refreshExpTime = jwtProperties.getRefresh().getExpiration();
    }

    public String createAccessToken(Long memberId, String role) {

        return Jwts.builder()
                .claim("memberId", memberId)
                .claim("tokenType", "access")
                .claim("role", role)
                .setExpiration(new Date(System.currentTimeMillis() + accessExpTime))
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();
    }
    public String createRefreshToken(Long memberId, String role) {

        return Jwts.builder()
                .claim("memberId", memberId)
                .claim("tokenType", "refresh")
                .claim("role", role)
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpTime))
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Token 에서 memberId 추출
    public Long getMemberIdFromToken(String token, String type) {
        return parseClaims(token, type).get("memberId", Long.class);
    }

    // JWT 검증
    public boolean validateAccessToken(String access) {
        try {
            if(!"access".equals(parseClaims(access, "access").get("tokenType", String.class)))
                throw new IllegalArgumentException("토큰 타입이 유효하지 않습니다.");
            return true;
        }catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.: {}", e.getMessage());
        }
        return false;
    }

    // JWT 검증
    public boolean validateRefreshToken(String refresh) {
        try {
            if(!"refresh".equals(parseClaims(refresh, "refresh").get("tokenType", String.class))) {
                throw new RestApiException(TokenErrorCode.INVALID_TOKEN_TYPE);
            }
            return true;
        }catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.: {}", e.getMessage());
        }
        return false;
    }

    // JWT Claims 추출
    public Claims parseClaims(String token, String type) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(type.equals("access") ? accessKey : refreshKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
