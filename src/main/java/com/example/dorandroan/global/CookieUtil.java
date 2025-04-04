package com.example.dorandroan.global;

import com.example.dorandroan.global.error.CommonErrorCode;
import com.example.dorandroan.global.error.TokenErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

@Component
@Slf4j
public class CookieUtil {

    @Value("${jwt.access.expiration}")
    private long accessExp;

    @Value("${jwt.refresh.expiration}")
    private long refreshExp;

    public void setTokenCookies(HttpServletRequest request, HttpServletResponse response,
                                String accessToken, String refreshToken, boolean isDeletion) {
        /*String host = request.getHeader("X-Forwarded-Host");
        if (host == null) host = request.getHeader("Host");*/

        /*String domain = null;
        if (host != null) {
            if (host.contains("dorandoran.online")) {
                domain = ".dorandoran.online";
            } else if (host.contains("localhost")) {
                domain = null;
            }
        }*/

        ResponseCookie.ResponseCookieBuilder accessTokenCookie = ResponseCookie.from("access", accessToken)
                .httpOnly(true)
                .path("/");
        ResponseCookie.ResponseCookieBuilder refreshTokenCookie = ResponseCookie.from("refresh", refreshToken)
                .httpOnly(true)
                .path("/");
        ResponseCookie.ResponseCookieBuilder localAccessTokenCookie = ResponseCookie.from("access", accessToken)
                .httpOnly(true)
                .path("/");
        ResponseCookie.ResponseCookieBuilder localRefreshTokenCookie = ResponseCookie.from("refresh", refreshToken)
                .httpOnly(true)
                .path("/");

        if (isDeletion) {
            accessTokenCookie.maxAge(0);
            refreshTokenCookie.maxAge(0);
            localAccessTokenCookie.maxAge(0);
            localRefreshTokenCookie.maxAge(0);
        } else {
            accessTokenCookie.maxAge((int) (accessExp / 1000));
            refreshTokenCookie.maxAge((int) (refreshExp / 1000));
            localAccessTokenCookie.maxAge((int) (accessExp / 1000));
            localRefreshTokenCookie.maxAge((int) (refreshExp / 1000));
        }

        /*if (domain != null) {
            accessTokenCookie.domain(domain);
            refreshTokenCookie.domain(domain);
            accessTokenCookie.secure(true);
            refreshTokenCookie.secure(true);
            accessTokenCookie.sameSite("None");
            refreshTokenCookie.sameSite("None");
        }*/
        accessTokenCookie.domain(".dorandoran.online");
        refreshTokenCookie.domain(".dorandoran.online");
        accessTokenCookie.secure(true);
        refreshTokenCookie.secure(true);
        accessTokenCookie.sameSite("None");
        refreshTokenCookie.sameSite("None");

        localAccessTokenCookie.domain("localhost");
        localRefreshTokenCookie.domain("localhost");

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.build().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.build().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, localAccessTokenCookie.build().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, localRefreshTokenCookie.build().toString());
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
        throw new RestApiException(TokenErrorCode.INVALID_REFRESH_TOKEN);
    }

    public void printHeader(HttpServletRequest request) {
        System.out.println("\nIn the Print Method First");
        System.out.println("Request Method: " + request.getMethod());

        // 2. 요청 URI
        System.out.println("Request URI: " + request.getRequestURI());

        // 3. 요청 URL
        System.out.println("Request URL: " + request.getRequestURL());

        // 4. 쿼리 파라미터 출력
        /*System.out.println("Query Parameters: ");
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            for (String value : paramValues) {
                System.out.println(paramName + " = " + value);
            }
        }*/

        // 5. 요청 헤더 출력
        System.out.println("Request Headers: ");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            System.out.println(headerName + " = " + headerValue);
        }

        // 6. 요청 속성 출력
        /*System.out.println("Request Attributes: ");
        Enumeration<String> attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            Object attributeValue = request.getAttribute(attributeName);
            System.out.println(attributeName + " = " + attributeValue);
        }*/

        // 7. 쿠키 출력
        System.out.println("Cookies: ");
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                System.out.println("Cookie: " + cookie.getName() + " = " + cookie.getValue());
            }
        }
    }
}
