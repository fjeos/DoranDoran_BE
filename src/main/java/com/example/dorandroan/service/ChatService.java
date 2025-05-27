package com.example.dorandroan.service;

import com.example.dorandroan.dto.ChatDto;
import com.example.dorandroan.dto.ChatResponseDto;
import com.example.dorandroan.entity.GroupChat;
import com.example.dorandroan.entity.Member;
import com.example.dorandroan.entity.PrivateChat;
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

import java.time.LocalDateTime;
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
        log.info("Inner Service");
        Member sender = memberService.findMember(memberId);

        GroupChat newChat = groupChatRepository.save(GroupChat.builder().senderId(sender.getMemberId())
                .groupChatId(UUID.randomUUID().toString())
                .chatRoomId(roomId)
                .content(chatDto.getContent())
                .type(chatDto.getType())
                .sendAt(LocalDateTime.now())
                .build());
        log.info("DTO Built");
        template.convertAndSend("/group/" + roomId, ChatResponseDto.toDto(newChat, sender));
    }

    @Transactional
    public void sendPrivateMessage(Long roomId, Long memberId, ChatDto chatDto) {
        Member sender = memberService.findMember(memberId);

        PrivateChat newChat = privateChatRepository.save(PrivateChat.builder().senderId(sender.getMemberId())
                .privateChatId(UUID.randomUUID().toString())
                .chatRoomId(roomId)
                .content(chatDto.getContent())
                .type(chatDto.getType())
                .sendAt(LocalDateTime.now())
                .build());
        template.convertAndSend("/private/" + roomId, ChatResponseDto.toDto(newChat, sender));
    }


}
