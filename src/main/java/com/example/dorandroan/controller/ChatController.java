package com.example.dorandroan.controller;

import com.example.dorandroan.entity.Chat;
import com.example.dorandroan.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatRepository chatRepository;

    @MessageMapping("/chat/send") // /app/chat/send 로 전송
    @SendTo("/topic/messages")    // 구독 주소
    public String sendMessage(String message) {
        return message;
    }

    @GetMapping("/chat")
    public ResponseEntity<Void> findChatBySenderId(Long senderId) {
        chatRepository.findBySenderId(senderId);
        return ResponseEntity.ok().build();
    }
}
