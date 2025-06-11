package com.example.dorandroan.dto;

import com.example.dorandroan.entity.MessageType;
import lombok.Getter;

@Getter
public class ChatDto {
    private String content;
    private MessageType type;
}
