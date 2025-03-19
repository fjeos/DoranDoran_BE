package com.example.dorandroan.global;

import com.example.dorandroan.global.error.CommonErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
public class CookieUtil {

    @Value("${jwt.access.expiration}")
    private long accessExp;

    @Value("${jwt.refresh.expiration}")
    private long refreshExp;

    public void setTokenCookies(HttpServletRequest request, HttpServletResponse response,
                                String accessToken, String refreshToken) {
        String host = request.getHeader("Host");
        String domain = (host != null && host.contains(".dorandoran.online"))? ".dorandoran.online" : null;

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
            accessTokenCookie.secure(true);
            refreshTokenCookie.secure(true);
        }
        accessTokenCookie.sameSite("None");
        refreshTokenCookie.sameSite("None");

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.build().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.build().toString());
    }
}
