package com.example.dorandroan.global.error;

import com.example.dorandroan.global.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    // 400 Bad Request
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "중복된 닉네임입니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "이미 가입된 이메일입니다."),
    UNMATCHED_TOKEN(HttpStatus.BAD_REQUEST, "토큰 정보가 일치하지 않습니다."),
    MISMATCH_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, "닉네임 형식이 올바르지 않습니다."),
    MISMATCH_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "비밀번호 형식이 올바르지 않습니다."),
    MISMATCH_IMG_URL_FORMAT(HttpStatus.BAD_REQUEST, "이미지 URL 형식이 올바르지 않습니다."),


    // 403 Forbidden

    // 404 Not Found
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원정보를 찾을 수 없습니다."),

    // 500 Internal Server Error
    MAIL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "메일 발송 중 오류가 발생했습니다.");


    private final HttpStatus status;
    private final String message;

}
