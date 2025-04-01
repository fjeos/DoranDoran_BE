package com.example.dorandroan.global;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private String message;

    public static ErrorResponse of(String errorMsg, String msg) {
        return ErrorResponse.builder().error(errorMsg).message(msg).build();
    }
}
