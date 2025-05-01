package com.example.dorandroan.controller;

import com.example.dorandroan.dto.ChatDto;
import com.example.dorandroan.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatDto chatDto, Message<?> messageObj) {
        SimpMessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(messageObj, SimpMessageHeaderAccessor.class);
        chatService.sendMessage(roomId, (Long) accessor.getSessionAttributes().get("memberId"), chatDto);
    }

}
