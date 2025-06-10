package com.example.dorandroan.service;

import com.example.dorandroan.dto.ChatDto;
import com.example.dorandroan.dto.ChatResponseDto;
import com.example.dorandroan.entity.*;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.MemberErrorCode;
import com.example.dorandroan.repository.GroupChatRepository;
import com.example.dorandroan.repository.GroupChatRoomRepository;
import com.example.dorandroan.repository.PrivateChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {
    private final MemberService memberService;
    private final GroupChatRepository groupChatRepository;
    private final PrivateChatRepository privateChatRepository;
    private final SimpMessagingTemplate template;

    @Transactional
    public void sendGroupMessage(Long roomId, Long memberId, ChatDto chatDto) {
        Member sender = memberService.findMember(memberId);

        GroupChat newChat = groupChatRepository.save(GroupChat.builder().senderId(sender.getMemberId())
                .groupChatId(UUID.randomUUID().toString())
                .chatRoomId(roomId)
                .content(chatDto.getContent())
                .type(chatDto.getType())
                .sendAt(Instant.now())
                .build());
        template.convertAndSend("/sub/group/" + roomId, ChatResponseDto.toDto(newChat, sender));
    }

    @Transactional
    public void sendSystemMessage(Long roomId, boolean isGroup, String message) {
        ChatResponseDto systemChat;
        if (isGroup) {
            systemChat = ChatResponseDto.toDto(groupChatRepository.save(GroupChat.builder()
                    .senderId(-1L).groupChatId(UUID.randomUUID().toString())
                    .chatRoomId(roomId).content(message).type("system").sendAt(Instant.now()).build()), null);
        } else {
            systemChat = ChatResponseDto.toDto(privateChatRepository.save(PrivateChat.builder().chatRoomId(roomId)
                    .privateChatId(UUID.randomUUID().toString())
                    .type("system").senderId(-1L).sendAt(Instant.now()).build()), null);
        }
        String destination = isGroup? "group" : "private";
        template.convertAndSend("/sub/" + destination + "/" + roomId, systemChat);
    }

    @Transactional
    public void sendPrivateMessage(Long roomId, Long memberId, ChatDto chatDto) {
        Member sender = memberService.findMember(memberId);

        PrivateChat newChat = privateChatRepository.save(PrivateChat.builder().senderId(sender.getMemberId())
                .privateChatId(UUID.randomUUID().toString())
                .chatRoomId(roomId)
                .content(chatDto.getContent())
                .type(chatDto.getType())
                .sendAt(Instant.now())
                .build());
        template.convertAndSend("/sub/private/" + roomId, ChatResponseDto.toDto(newChat, sender));
    }


}
