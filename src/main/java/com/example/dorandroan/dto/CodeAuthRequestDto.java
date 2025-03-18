package com.example.dorandroan.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CodeAuthRequestDto {
    private Integer authCode;
    private String clientCode;
}
