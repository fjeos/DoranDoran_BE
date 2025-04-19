package com.example.dorandroan.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Getter
public class ChatRoomRequestDto {

    @NotBlank(message = "제목은 공백일 수 없습니다.")
    @Size(min=1, max=15)
    private String chatRoomTitle;

    private String chatRoomImage;

    @Range(min=3, max=100)
    private Integer maxCount;

    @Size(max=255)
    private String description;
}
