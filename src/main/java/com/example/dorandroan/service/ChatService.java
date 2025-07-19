package com.example.dorandroan.service;

import com.example.dorandroan.dto.ChatDto;
import com.example.dorandroan.dto.ChatResponseDto;
import com.example.dorandroan.entity.*;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.ChattingErrorCode;
import com.example.dorandroan.global.error.CommonErrorCode;
import com.google.api.Http;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
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
    public void sendGroupMessage(Long roomId, Long memberId, ChatDto chatDto) throws FirebaseMessagingException, IOException {
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
            List<Member> groupChatroomMembers = chatAndMemberUtil.findGroupChatroomMembers(roomId);
            sendGroupRoomAlert(groupChatroomMembers);
            sendGroupFcm(groupChatroomMembers, sender, groupChatroomService.findChatroom(roomId).getTitle(), chatDto.getContent());
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
        sendGroupRoomAlert(chatAndMemberUtil.findGroupChatroomMembers(roomId));
    }

    @Transactional
    public void sendPrivateMessage(Long roomId, Long memberId, ChatDto chatDto) throws FirebaseMessagingException, IOException {
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
            sendPrivateFcm(sender, roomId, chatDto.getContent());
        }
    }

    public void sendAlert(Long memberId) {
        template.convertAndSend("/sub/personal/" + memberId, "");
    }

    private void sendGroupRoomAlert(List<Member> members) {
        members.forEach(m -> sendAlert(m.getMemberId()));
    }

    private void sendGroupFcm(List<Member> members, Member sender, String title, String body) {
        members.forEach(m -> {
            if (!m.equals(sender)) {
                try {
                    fcmService.sendFcmMessage(m, title, body);
                } catch (FirebaseMessagingException e) {
                    throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR);
                }
            }
        });
    }
    private boolean validateChattingMember(Member member, Long roomId, boolean isGroup) {
        if (isGroup) {
            return !groupChatroomService.validateChattingMember(member.getMemberId(), roomId);
        } else {
            return privateChatroomService.validateChattingMember(member, roomId).isEmpty();
        }
    }

    public void sendPrivateFcm(Member member, Long roomId, String content) throws FirebaseMessagingException, IOException {
        Member other = chatAndMemberUtil.findOtherAtPrivateChatroom(roomId, member);
        fcmService.sendFcmMessage(other, other.getNickname(), content);
    }
}
