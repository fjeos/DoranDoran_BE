package com.example.dorandroan.global.error;

import com.example.dorandroan.global.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    // 400 Bad Request

    // 401 Unauthorized

    // 403 Forbidden

    // 404 Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원정보를 찾을 수 없습니다.");


    private final HttpStatus status;
    private final String message;

}
