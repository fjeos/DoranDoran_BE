package com.example.dorandroan.global.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.sql.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final long accessExpTime;
    private final long refreshExpTime;
    private final Key accessKey;
    private final Key refreshKey;

    public JwtUtil(final String accessSecret,
                   final String refreshSecret,
                   final long accessExpTime,
                   final long refreshExpTime) {
        byte[] accessBytes = Decoders.BASE64.decode(accessSecret);
        byte[] refreshBytes = Decoders.BASE64.decode(refreshSecret);
        this.accessKey = Keys.hmacShaKeyFor(accessBytes);
        this.refreshKey = Keys.hmacShaKeyFor(refreshBytes);
        this.accessExpTime = accessExpTime;
        this.refreshExpTime = refreshExpTime;
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
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    // JWT 검증
    public boolean validateRefreshToken(String refresh) {
        try {
            if(!"refresh".equals(parseClaims(refresh, "refresh").get("tokenType", String.class)))
                throw new IllegalArgumentException("토큰 타입이 유효하지 않습니다.");
            return true;
        }catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
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
