package com.example.dorandroan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ChatRoomImageUpdateDto {

    private Long chatRoomId;

    @NotBlank(message = "이미지는 공백일 수 없습니다.")
    private String chatRoomImage;
}
