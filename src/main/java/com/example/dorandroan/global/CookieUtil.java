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

@Component
public class CookieUtil {

    @Value("${jwt.access.expiration}")
    private long accessExp;

    @Value("${jwt.refresh.expiration}")
    private long refreshExp;

    public void setTokenCookies(HttpServletRequest request, HttpServletResponse response,
                                String accessToken, String refreshToken) {
        String origin = request.getHeader("Origin");
        String domain = null;

        if (origin != null) {
            try {
                URL url = new URL(origin);
                domain = url.getHost();
            } catch (MalformedURLException e) {
                throw new RestApiException(CommonErrorCode.INVALID_PARAMETER);
            }
        }

        ResponseCookie.ResponseCookieBuilder accessTokenCookie = ResponseCookie.from("access", accessToken)
                .httpOnly(true)
                .path("/")
                .maxAge((int)(accessExp / 1000));
        ResponseCookie.ResponseCookieBuilder refreshTokenCookie = ResponseCookie.from("refresh", refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge((int)(refreshExp / 1000));

        if (domain != null && !domain.equals("localhost")) {
            System.out.println("=============There is Domain : " + domain + "=================");
            accessTokenCookie.domain(domain);
            refreshTokenCookie.domain(domain);
        } else {
            System.out.println("OHohOHhohohOH There is no domain~~~~~~");
        }

        if (origin != null && origin.startsWith("https")) {
            accessTokenCookie.secure(true);
            refreshTokenCookie.secure(true);
        }


//        Cookie accessTokenCookie = new Cookie("access", accessToken);
//        accessTokenCookie.setHttpOnly(true);
//        accessTokenCookie.setSecure(true);
//        accessTokenCookie.setPath("/");
//        accessTokenCookie.setMaxAge((int)(accessExp / 1000));  // 30분
//
//        Cookie refreshTokenCookie = new Cookie("refresh", refreshToken);
//        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setSecure(true);
//        refreshTokenCookie.setPath("/");
//        refreshTokenCookie.setMaxAge((int)(refreshExp / 1000));  // 7일
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.build().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.build().toString());
//        response.addCookie(accessTokenCookie);
//        response.addCookie(refreshTokenCookie);
    }
}
