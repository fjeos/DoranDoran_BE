package com.example.dorandroan.controller;

import com.example.dorandroan.dto.ChatDto;
import com.example.dorandroan.global.ErrorResponse;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.ChattingErrorCode;
import com.example.dorandroan.service.ChatService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate template;
    @MessageMapping("/group/{roomId}")
    public void sendGroupMessage(@DestinationVariable Long roomId, ChatDto chatDto, Message<?> messageObj) throws FirebaseMessagingException, IOException {
        SimpMessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(messageObj, SimpMessageHeaderAccessor.class);
        Long memberId = (Long) accessor.getSessionAttributes().get("memberId");
        chatService.sendGroupMessage(roomId, memberId, chatDto);
    }

    @MessageMapping("/private/{roomId}")
    public void sendPrivateMessage(@DestinationVariable Long roomId, ChatDto chatDto, Message<?> messageObj) throws FirebaseMessagingException, IOException {
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
        Object memberId = accessor.getSessionAttributes().get("memberId");
        ErrorResponse errorResponse;

        if (ex instanceof RestApiException restEx) {
            ChattingErrorCode errorCode = (ChattingErrorCode) restEx.getErrorCode();
            errorResponse = ErrorResponse.of(String.valueOf(errorCode.getStatus()), errorCode.getMessage());
        } else {
            errorResponse = ErrorResponse.of(ex.getMessage(), ex.toString());
        }
        template.convertAndSend("/sub/personal/" + memberId, errorResponse);
    }
}
