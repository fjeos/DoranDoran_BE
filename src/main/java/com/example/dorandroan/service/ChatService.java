package com.example.dorandroan.service;

import com.example.dorandroan.entity.Chat;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.MemberErrorCode;
import com.example.dorandroan.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;

    public void findChatBySenderId(Long senderId) {
        Chat chat = chatRepository.findBySenderId(senderId).orElseThrow(() -> new RestApiException(MemberErrorCode.UNAPPROVED_EMAIL));
        System.out.println("============FIND CHAT============");
        System.out.println(chat.getChatId());
        System.out.println(chat.getSenderId());
        System.out.println(chat.getContent());
        System.out.println(chat.getType());
        System.out.println("=================================");
    }
}
