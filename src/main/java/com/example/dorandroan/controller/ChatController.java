package com.example.dorandroan.controller;

import com.example.dorandroan.dto.ChatDto;
import com.example.dorandroan.dto.ChatRoomRequestDto;
import com.example.dorandroan.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/chatrooms")
    public ResponseEntity<Map<String, Long>> createChatRoom(@Valid @RequestBody ChatRoomRequestDto requestDto) {

        return ResponseEntity.ok(Map.of("chatRoomId", chatService.createChatRoom(requestDto)));
    }

    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatDto chatDto, Message<?> messageObj) {
        SimpMessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(messageObj, SimpMessageHeaderAccessor.class);
        chatService.sendMessage(roomId, (Long) accessor.getSessionAttributes().get("memberId"), chatDto);
    }

    @GetMapping("/chat")
    public ResponseEntity<Void> findChatBySenderId(Long senderId) {
        chatService.findChatBySenderId(senderId);
        return ResponseEntity.ok().build();
    }
}
