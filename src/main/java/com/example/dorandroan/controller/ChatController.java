package com.example.dorandroan.controller;

import com.example.dorandroan.dto.ChatDto;
import com.example.dorandroan.global.ErrorCode;
import com.example.dorandroan.global.ErrorResponse;
import com.example.dorandroan.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate template;
    @MessageMapping("/group/{roomId}")
    public void sendGroupMessage(@DestinationVariable Long roomId, ChatDto chatDto, Message<?> messageObj) {
        SimpMessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(messageObj, SimpMessageHeaderAccessor.class);
        Long memberId = (Long) accessor.getSessionAttributes().get("memberId");
        chatService.sendGroupMessage(roomId, memberId, chatDto);
    }

    @MessageMapping("/private/{roomId}")
    public void sendPrivateMessage(@DestinationVariable Long roomId, ChatDto chatDto, Message<?> messageObj) {
        SimpMessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(messageObj, SimpMessageHeaderAccessor.class);
        chatService.sendPrivateMessage(roomId, (Long) accessor.getSessionAttributes().get("memberId"), chatDto);
    }

    @MessageMapping("/personal/{memberId}")
    public void sendAlert(@DestinationVariable Long memberId) {
        chatService.sendAlert(memberId);
    }

    @MessageExceptionHandler
    public void handleException(Throwable ex, Message<?> messageObj) {
        SimpMessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(messageObj, SimpMessageHeaderAccessor.class);
        template.convertAndSend("/personal/" +
                accessor.getSessionAttributes().get("memberId"), new ErrorResponse(ex.getMessage(), ex.toString()));
    }
}
