package com.example.dorandroan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ChatRoomTitleUpdateDto {

    private Long chatRoomId;

    @NotBlank(message = "제목은 공백일 수 없습니다.")
    @Size(min=1, max=15)
    private String chatRoomTitle;
}
