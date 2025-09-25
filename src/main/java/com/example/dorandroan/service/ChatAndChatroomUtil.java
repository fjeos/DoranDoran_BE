package com.example.dorandroan.service;

import com.example.dorandroan.dto.*;
import com.example.dorandroan.entity.*;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.ChattingErrorCode;
import com.example.dorandroan.repository.GroupChatRoomRepository;
import com.example.dorandroan.repository.MemberChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatAndChatroomUtil {

    private final GroupChatRoomRepository groupChatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final MemberService memberService;
    private final ChatService chatService;

    @Transactional
    public Long createGroupChatroom(Member member, ChatRoomRequestDto requestDto) {

        GroupChatroom newChatRoom = groupChatRoomRepository.save(GroupChatroom.builder().
                title(requestDto.getChatRoomTitle())
                .chatRoomImg(requestDto.getChatRoomImage())
                .description(requestDto.getDescription())
                .maxPartIn(requestDto.getMaxCount())
                .nowPartIn(1)
                .closed(false)
                .build());
        memberChatRoomRepository.save(MemberChatroom.builder().member(member)
                .role(ChatRoomRole.LEAD)
                .groupChatroom(newChatRoom).build());

        chatService.sendSystemMessage(newChatRoom.getGroupChatroomId(), MessageType.system, "채팅방이 생성되었습니다.");
        return newChatRoom.getGroupChatroomId();
    }

    @Transactional
    public void enterGroupChatroom(Member member, Long chatRoomId) {

        GroupChatroom groupChatroom = groupChatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND));

        if (memberChatRoomRepository.existsByMember_MemberIdAndGroupChatroom_GroupChatroomIdAndLeaveTimeIsNull(
                member.getMemberId(), chatRoomId))
            throw new RestApiException(ChattingErrorCode.ALREADY_JOINED);

        if (groupChatroom.getMaxPartIn() <= groupChatroom.getNowPartIn())
            throw new RestApiException(ChattingErrorCode.FULL_CHATROOM);

        memberChatRoomRepository.save(MemberChatroom.builder()
                .groupChatroom(groupChatroom)
                .member(memberService.findMember(member.getMemberId()))
                .role(ChatRoomRole.PART)
                .joinTime(Instant.now())
                .build());
        chatService.sendSystemMessage(chatRoomId, MessageType.system, member.getNickname() + "님이 입장하셨습니다.");
        groupChatroom.enterRoom();
    }

    @Transactional
    public void changeRoomTitle(Member member, ChatRoomTitleUpdateDto requestDto) {
        getMemberChatroomForUpdate(member, requestDto.getChatRoomId())
                .changeTitle(requestDto.getChatRoomTitle());
        chatService.sendSystemMessage(requestDto.getChatRoomId(), MessageType.change, "채팅방 제목이 변경되었습니다.");
    }

    @Transactional
    public void changeRoomImage(Member member, ChatRoomImageUpdateDto requestDto) {
        getMemberChatroomForUpdate(member, requestDto.getChatRoomId())
                .changeRoomImage(requestDto.getChatRoomImage());
        chatService.sendSystemMessage(requestDto.getChatRoomId(), MessageType.change, "채팅방 이미지가 변경되었습니다.");
    }

    @Transactional
    public void changeRoomMaxCount(Member member, ChatRoomMaxUpdateDto requestDto) {
        GroupChatroom chatroom = getMemberChatroomForUpdate(member, requestDto.getChatRoomId());
        if (requestDto.getMaxCount() < chatroom.getNowPartIn())
            throw new RestApiException(ChattingErrorCode.LESS_THAN_NOW);
        chatroom.changeMaxCount(requestDto.getMaxCount());
        chatService.sendSystemMessage(requestDto.getChatRoomId(), MessageType.change, "채팅방 최대 인원이 변경되었습니다.");
    }

    @Transactional
    public void changeRoomDescription(Member member, ChatRoomDescriptionUpdateDto requestDto) {
        getMemberChatroomForUpdate(member, requestDto.getChatRoomId())
                .changeDescription(requestDto.getDescription());
        chatService.sendSystemMessage(requestDto.getChatRoomId(), MessageType.change, "채팅방 설명이 변경되었습니다.");
    }

    @Transactional
    public void deleteGroupChatRoom(Member member, Long chatRoomId) {
        GroupChatroom chatroom = getMemberChatroomForUpdate(member, chatRoomId);
        chatroom.delete();
        chatroom.closeChatroom();
        memberChatRoomRepository.findChatRoomByMemberAndChatRoomId(member, chatRoomId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND)).leave();
        chatService.sendSystemMessage(chatRoomId, MessageType.system, "채팅방이 폐쇄되었습니다.");
    }

    @Transactional
    public void outOfGroupChatRoom(Member member, Long groupId) {
        MemberChatroom chatroom = memberChatRoomRepository.findChatRoomByMemberAndChatRoomId(member, groupId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND));
        if (chatroom.getLeaveTime() != null)
            throw new RestApiException(ChattingErrorCode.NOT_PART_IN);
        chatService.sendSystemMessage(groupId, MessageType.system, member.getNickname() + "님이 퇴장하셨습니다.");
        chatroom.leave();
        groupChatRoomRepository.findById(groupId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND)).leaveRoom();
    }

    private GroupChatroom getMemberChatroomForUpdate(Member member, Long chatRoomId) {
        MemberChatroom chatroom = memberChatRoomRepository.findChatRoomByMemberAndChatRoomIdAndNotClosed(member, chatRoomId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND));
        if (!chatroom.getRole().equals(ChatRoomRole.LEAD))
            throw new RestApiException(ChattingErrorCode.NOT_LEAD);
        if (chatroom.getGroupChatroom().isClosed())
            throw new RestApiException(ChattingErrorCode.ALREADY_CLOSED);
        return chatroom.getGroupChatroom();
    }
}
