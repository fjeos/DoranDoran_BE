package com.example.dorandroan.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SystemMessageDto {
    private String chatId;
    private Long senderId;
    private String type;
    private String content;
}
