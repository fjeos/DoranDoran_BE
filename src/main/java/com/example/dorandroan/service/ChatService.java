package com.example.dorandroan.service;

import com.example.dorandroan.dto.ChatDto;
import com.example.dorandroan.dto.ChatResponseDto;
import com.example.dorandroan.entity.*;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.ChattingErrorCode;
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
    private final GroupChatroomService groupChatroomService;
    private final PrivateChatroomService privateChatroomService;
    private final SimpMessagingTemplate template;
    private final FcmService fcmService;

    @Transactional
    public void sendGroupMessage(Long roomId, Long memberId, ChatDto chatDto) {
        Member sender = memberService.findMember(memberId);
        if (validateChattingMember(sender, roomId, true))
            throw new RestApiException(ChattingErrorCode.NOT_PART_IN);

        if (chatDto.getContent() == null) {
            MemberChatroom chatroom = groupChatroomService.findMemberChatroom(roomId);

            switch (chatDto.getType()) {
                case enter -> chatroom.enter();
                case leave -> chatroom.leave();
                default -> throw new RestApiException(ChattingErrorCode.INVALID_TYPE);
            }
        } else {
            GroupChat newChat = groupChatroomService.saveChat(GroupChat.builder().senderId(sender.getMemberId())
                    .groupChatId(UUID.randomUUID().toString())
                    .chatRoomId(roomId)
                    .content(chatDto.getContent())
                    .type(chatDto.getType())
                    .sendAt(Instant.now())
                    .build());
            template.convertAndSend("/sub/group/" + roomId, ChatResponseDto.toDto(newChat, sender));
            sendGroupRoomAlert(roomId);
            fcmService.sendFcmMessage(sender, group);
        }
    }

    @Transactional
    public void sendSystemMessage(Long roomId, MessageType type, String message) {
        template.convertAndSend("/sub/group/" + roomId,
                ChatResponseDto.toDto(groupChatroomService.saveChat(
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
            PrivateChatroom chatroom = privateChatroomService.findById(roomId);
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
            PrivateChat newChat = privateChatroomService.saveChat(PrivateChat.builder().senderId(sender.getMemberId())
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
            return !groupChatroomService.validateChattingMember(member.getMemberId(), roomId);
        } else {
            return privateChatroomService.validateChattingMember(member, roomId).isEmpty();
        }
    }
}
