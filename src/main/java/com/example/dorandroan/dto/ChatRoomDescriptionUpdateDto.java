package com.example.dorandroan.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ChatRoomDescriptionUpdateDto {

    private Long chatRoomId;

    @Size(max=255)
    private String description;
}
