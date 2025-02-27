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

    @Value("${jwt.access.secret}")
    String accessSecret;
    @Value("${jwt.refresh.secret}")
    String refreshSecret;
    @Value("${jwt.access.expiration}")
    private final long accessExpTime;
    @Value("${jwt.refresh.expiration}")
    private final long refreshExpTime;
    private final Key accessKey;
    private final Key refreshKey;

    public JwtUtil() {
        byte[] accessBytes = Decoders.BASE64.decode(accessSecret);
        byte[] refreshBytes = Decoders.BASE64.decode(refreshSecret);
        this.accessKey = Keys.hmacShaKeyFor(accessBytes);
        this.refreshKey = Keys.hmacShaKeyFor(refreshBytes);
    }

    public String createAccessToken(Long memberId) {
        Claims claims = Jwts.claims();
        claims.put("memberId", memberId);
        claims.put("tokenType", "access");
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + accessExpTime))
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();
    }
    public String createRefreshToken(Long memberId) {
        Claims claims = Jwts.claims();
        claims.put("memberId", memberId);
        claims.put("tokenType", "refresh");
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + accessExpTime))
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Token 에서 memberId 추출
    public Long getMemberIdFromToken(String token) {
        return parseClaims(token).get("memberId", Long.class);
    }

    // JWT 검증
    public boolean validateAccessToken(String access) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(access).getBody();
            if(!"refresh".equals(claims.get("tokenType", String.class)))
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
}
