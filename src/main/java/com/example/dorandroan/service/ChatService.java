package com.example.dorandroan.service;

import com.example.dorandroan.dto.ChatDto;
import com.example.dorandroan.dto.ChatResponseDto;
import com.example.dorandroan.entity.*;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.ChattingErrorCode;
import com.example.dorandroan.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {
    private final MemberService memberService;
    private final ChatAndMemberUtil chatAndMemberUtil;
    private final GroupChatRepository groupChatRepository;
    private final PrivateChatRepository privateChatRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final PrivateChatroomRepository privateChatroomRepository;
    private final SimpMessagingTemplate template;

    @Transactional
    public void sendGroupMessage(Long roomId, Long memberId, ChatDto chatDto) {
        Member sender = memberService.findMember(memberId);
        if (validateChattingMember(sender, roomId, true))
            throw new RestApiException(ChattingErrorCode.NOT_PART_IN);

        if (chatDto.getContent() == null) {
            MemberChatroom chatroom = memberChatRoomRepository.findById(roomId)
                    .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND));

            switch (chatDto.getType()) {
                case enter -> chatroom.enter();
                case leave -> chatroom.leave();
                default -> throw new RestApiException(ChattingErrorCode.INVALID_TYPE);
            }
        } else {
            GroupChat newChat = groupChatRepository.save(GroupChat.builder().senderId(sender.getMemberId())
                    .groupChatId(UUID.randomUUID().toString())
                    .chatRoomId(roomId)
                    .content(chatDto.getContent())
                    .type(chatDto.getType())
                    .sendAt(Instant.now())
                    .build());
            template.convertAndSend("/sub/group/" + roomId, ChatResponseDto.toDto(newChat, sender));
            sendGroupRoomAlert(roomId);
        }
    }

    @Transactional
    public void sendSystemMessage(Long roomId, MessageType type, String message) {
        template.convertAndSend("/sub/group/" + roomId,
                ChatResponseDto.toDto(groupChatRepository.save(
                        GroupChat.builder()
                                .senderId(-1L).groupChatId(UUID.randomUUID().toString())
                                .chatRoomId(roomId).content(message).type(type)
                                .sendAt(Instant.now()).build()), null));
        sendGroupRoomAlert(roomId);
    }

    @Transactional
    public void sendPrivateMessage(Long roomId, Long memberId, ChatDto chatDto) {
        Member sender = memberService.findMember(memberId);
        if (validateChattingMember(sender, roomId, false))
            throw new RestApiException(ChattingErrorCode.NOT_PART_IN);

        if (chatDto.getContent() == null) {
            PrivateChatroom chatroom = privateChatroomRepository.findById(roomId)
                    .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND));
            boolean amIMemberA = chatAndMemberUtil.amIMemberA(chatroom, sender);
            switch (chatDto.getType()) {
                case enter -> {
                    if (amIMemberA) chatroom.enterA();
                    else chatroom.enterB();
                }
                case leave -> {
                    if (amIMemberA) chatroom.leaveA();
                    else chatroom.leaveB();
                }
                default -> throw new RestApiException(ChattingErrorCode.INVALID_TYPE);
            }
        } else {
            PrivateChat newChat = privateChatRepository.save(PrivateChat.builder().senderId(sender.getMemberId())
                    .privateChatId(UUID.randomUUID().toString())
                    .chatRoomId(roomId)
                    .content(chatDto.getContent())
                    .type(chatDto.getType())
                    .sendAt(Instant.now())
                    .build());
            template.convertAndSend("/sub/private/" + roomId, ChatResponseDto.toDto(newChat, sender));
            sendAlert(chatAndMemberUtil.findOtherAtPrivateChatroom(roomId, sender).getMemberId());
        }
    }

    public void sendAlert(Long memberId) {
        template.convertAndSend("/sub/personal/" + memberId, "");
    }

    public void sendGroupRoomAlert(Long roomId) {
        chatAndMemberUtil.findGroupChatroomMembers(roomId).forEach(m -> sendAlert(m.getMemberId()));
    }

    private boolean validateChattingMember(Member member, Long roomId, boolean isGroup) {
        if (isGroup) {
            return !memberChatRoomRepository.
                    existsByMember_MemberIdAndGroupChatroom_GroupChatroomIdAndQuitFalse(member.getMemberId(), roomId);
        } else {
            return privateChatroomRepository.findChatroomByMemberAndNotQuit(member, roomId).isEmpty();
        }
    }
}
