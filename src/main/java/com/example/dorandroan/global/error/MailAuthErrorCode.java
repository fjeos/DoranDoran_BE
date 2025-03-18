package com.example.dorandroan.global.error;

import com.example.dorandroan.global.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MailAuthErrorCode implements ErrorCode {

    CANNOT_FOUND_CLIENT(HttpStatus.NOT_FOUND, "해당하는 이메일을 찾을 수 없습니다."),
    CANNOT_FOUND_CODE(HttpStatus.NOT_FOUND, "해당하는 코드가 없습니다."),

    CODE_UNMATCHED(HttpStatus.BAD_REQUEST, "코드가 일치하지 않습니다");

    private final HttpStatus status;
    private final String message;
}
