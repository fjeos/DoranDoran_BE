package com.example.dorandroan.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class EmailAuthRequestDto {
    @Email
    private String email;
    @NotNull(message = "필드 값을 입력하세요")
    private Boolean isSignUp;
}
