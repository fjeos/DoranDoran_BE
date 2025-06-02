package com.example.dorandroan.global;

import com.example.dorandroan.global.error.ChattingErrorCode;
import com.example.dorandroan.global.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String accessToken = cookieUtil.getAccessFromCookie(servletRequest.getServletRequest());

            // 토큰 검증 후 사용자 정보 저장
            if (jwtUtil.validateAccessToken(accessToken)) {
                attributes.put("memberId", jwtUtil.getMemberIdFromToken(accessToken, "access"));
                return true;
            }
        }
        ///setErrorResponse(response, ChattingErrorCode.HANDSHAKE_ERROR);
        return false;
    }
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}