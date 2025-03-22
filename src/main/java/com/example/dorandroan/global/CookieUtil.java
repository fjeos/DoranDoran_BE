package com.example.dorandroan.global;

import com.example.dorandroan.global.error.CommonErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class CookieUtil {

    @Value("${jwt.access.expiration}")
    private long accessExp;

    @Value("${jwt.refresh.expiration}")
    private long refreshExp;

    public void setTokenCookies(HttpServletRequest request, HttpServletResponse response,
                                String accessToken, String refreshToken) {
        String host = request.getHeader("X-Forwarded-Host");
        if (host == null) host = request.getHeader("Host");

        String domain = null;
        if (host != null) {
            if (host.contains("dorandoran.online")) {
                domain = ".dorandoran.online";
            } else if (host.contains("localhost")) {
                domain = null;
            }
        }

        ResponseCookie.ResponseCookieBuilder accessTokenCookie = ResponseCookie.from("access", accessToken)
                .httpOnly(true)
                .path("/")
                .maxAge((int) (accessExp / 1000));
        ResponseCookie.ResponseCookieBuilder refreshTokenCookie = ResponseCookie.from("refresh", refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge((int) (refreshExp / 1000));

        if (domain != null) {
            accessTokenCookie.domain(domain);
            refreshTokenCookie.domain(domain);
        }
        accessTokenCookie.secure(true);
        refreshTokenCookie.secure(true);
        accessTokenCookie.sameSite("None");
        refreshTokenCookie.sameSite("None");

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.build().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.build().toString());
    }

    // Cookie에서 access토큰 가져오기
    public String getAccessFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
    public String getRefreshFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        throw new RestApiException(CommonErrorCode.INVALID_PARAMETER);
    }
}
