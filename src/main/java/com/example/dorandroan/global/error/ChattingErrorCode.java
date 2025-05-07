package com.example.dorandroan.global.error;

import com.example.dorandroan.global.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChattingErrorCode implements ErrorCode {

    // 400 BAD REQUEST
    INVALID_MEMBER(HttpStatus.BAD_REQUEST, "해당 회원과 채팅을 시작할 수 없습니다."),

    // 404 NOT FOUND
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."),

    // 500 INTERNAL SERVER ERROR
    HANDSHAKE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "핸드셰이크 과정 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
