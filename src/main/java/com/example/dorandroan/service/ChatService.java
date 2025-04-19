package com.example.dorandroan.service;

import com.example.dorandroan.dto.ChatDto;
import com.example.dorandroan.dto.ChatResponseDto;
import com.example.dorandroan.dto.ChatRoomRequestDto;
import com.example.dorandroan.entity.Chat;
import com.example.dorandroan.entity.ChatRoom;
import com.example.dorandroan.entity.Member;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.ChattingErrorCode;
import com.example.dorandroan.global.error.MemberErrorCode;
import com.example.dorandroan.repository.ChatRepository;
import com.example.dorandroan.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {
    private final MemberService memberService;
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate template;
    private final ChatRoomRepository chatRoomRepository;

    public void findChatBySenderId(Long senderId) {
        Chat chat = chatRepository.findBySenderId(senderId).orElseThrow(() -> new RestApiException(MemberErrorCode.UNAPPROVED_EMAIL));
        System.out.println("============FIND CHAT============");
        System.out.println(chat.getChatId());
        System.out.println(chat.getSenderId());
        System.out.println(chat.getContent());
        System.out.println(chat.getType());
        System.out.println("=================================");
    }

    @Transactional
    public Long createChatRoom(ChatRoomRequestDto requestDto) {
        return chatRoomRepository.save(ChatRoom.builder().
                title(requestDto.getChatRoomTitle())
                .chatRoomImg(requestDto.getChatRoomImage())
                .description(requestDto.getDescription())
                .maxPartIn(requestDto.getMaxCount())
                .closed(false)
                .build()).getChatroomId();
    }

    @Transactional
    public void sendMessage(Long roomId, Long memberId, ChatDto chatDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND));
        Member sender = memberService.findMember(memberId);
        Member receiver;

        Chat newChat = chatRepository.save(Chat.builder().senderId(sender.getMemberId())
                .chatId(UUID.randomUUID().toString())
                .content(chatDto.getContent())
                .type(chatDto.getType())
                .sendAt(LocalDateTime.now())
                .build());
        template.convertAndSend("/chatRoom/" + roomId, ChatResponseDto.toDto(newChat, sender));
    }

}
