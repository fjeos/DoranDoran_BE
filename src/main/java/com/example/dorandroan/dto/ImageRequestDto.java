package com.example.dorandroan.dto;

import jakarta.validation.constraints.Max;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageRequestDto {
    private String fileName;
    @Max(value = 10, message = "파일 용량이 초과하였습니다.")
    private Integer fileSize;
}
