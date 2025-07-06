package com.example.dorandroan.service;

import com.example.dorandroan.dto.*;
import com.example.dorandroan.entity.*;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.ChattingErrorCode;
import com.example.dorandroan.repository.GroupChatRepository;
import com.example.dorandroan.repository.GroupChatRoomRepository;
import com.example.dorandroan.repository.MemberChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupChatroomService {

    private final GroupChatRoomRepository groupChatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final GroupChatRepository groupChatRepository;
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
                .quit(false)
                .groupChatroom(newChatRoom).build());

        chatService.sendSystemMessage(newChatRoom.getGroupChatroomId(), MessageType.system, "채팅방이 생성되었습니다.");
        return newChatRoom.getGroupChatroomId();
    }

    public List<ChatRoomMembersResponseDto> getGroupChatRoomMembers(Member member, Long chatRoomId) {

        List<Member> memberByChatRoom = memberChatRoomRepository.findMemberByChatRoom(chatRoomId);
        if (memberByChatRoom.isEmpty())
            throw new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND);

        if (memberByChatRoom.stream().noneMatch(m -> m.getMemberId().equals(member.getMemberId())))
            throw new RestApiException(ChattingErrorCode.NOT_PART_IN);

        return memberByChatRoom.stream()
                .map(ChatRoomMembersResponseDto::toDto).toList();
    }

    @Transactional
    public void enterGroupChatroom(Member member, Long chatRoomId) {

        GroupChatroom groupChatroom = groupChatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND));

        if (memberChatRoomRepository.existsByMember_MemberIdAndGroupChatroom_GroupChatroomIdAndQuitFalse(
                member.getMemberId(), chatRoomId))
            throw new RestApiException(ChattingErrorCode.ALREADY_JOINED);

        if (groupChatroom.getMaxPartIn() <= groupChatroom.getNowPartIn())
            throw new RestApiException(ChattingErrorCode.FULL_CHATROOM);

        memberChatRoomRepository.save(MemberChatroom.builder()
                .groupChatroom(groupChatroom)
                .member(memberService.findMember(member.getMemberId()))
                .role(ChatRoomRole.PART)
                .joinTime(Instant.now())
                .quit(false).build());
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
        getMemberChatroomForUpdate(member, chatRoomId).delete();
        chatService.sendSystemMessage(chatRoomId, MessageType.system, "채팅방이 폐쇄되었습니다.");
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

    @Transactional
    public void outOfGroupChatRoom(Member member, Long groupId) {
        MemberChatroom chatroom = memberChatRoomRepository.findChatRoomByMemberAndChatRoomId(member, groupId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND));
        if (chatroom.isQuit())
            throw new RestApiException(ChattingErrorCode.NOT_PART_IN);
        chatService.sendSystemMessage(groupId, MessageType.system, member.getNickname() + "님이 퇴장하셨습니다.");
        chatroom.out();
        groupChatRoomRepository.findById(groupId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND)).leaveRoom();
    }

    public ChatroomInfoResponseDto getGroupChatroomInfo(Member member, Long chatRoomId) {
        MemberChatroom chatroom = memberChatRoomRepository.findChatRoomByMemberAndChatRoomIdAndNotClosed(member, chatRoomId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND));
        return ChatroomInfoResponseDto.toDto(chatroom.getGroupChatroom(), chatroom.getRole().equals(ChatRoomRole.LEAD));
    }

    public List<ChatRoomListResponseDto> getGroupChatroomList(Long cursor, Integer limit) {


        return groupChatRoomRepository.findAllAndNotClosed(cursor, PageRequest.of(0, limit)).stream().map(
                c -> ChatRoomListResponseDto.toDto(c, groupChatRepository.findTopByChatRoomIdOrderBySendAtDesc(c.getGroupChatroomId()))
        ).collect(Collectors.toList());
    }

    public int countGroupUnreadChat(MemberChatroom memberChatroom) {
        return groupChatRepository.countByChatRoomIdAndSendAtAfter(memberChatroom.getGroupChatroom().getGroupChatroomId(),
                memberChatroom.getLeaveTime());
    }

    public List<MemberChatroom> findChatRoomByMemberAndNotQuit(Member member) {
        return memberChatRoomRepository.findChatRoomByMemberAndNotQuit(member);
    }

    public GroupChat findTop(Long chatroomId) {
        return groupChatRepository.findTopByChatRoomIdOrderBySendAtDesc(chatroomId);
    }

    public MemberChatroom findChatRoomByMemberAndChatRoomId(Member member, Long chatroomId) {

        return memberChatRoomRepository.findChatRoomByMemberAndChatRoomIdAndNotClosed(member, chatroomId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND));
    }

    public MemberChatroom findMemberChatroom(Long id) {
        return memberChatRoomRepository.findById(id)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND));
    }

    public boolean validateChattingMember(Long memberId, Long roomId) {
        return memberChatRoomRepository.
                existsByMember_MemberIdAndGroupChatroom_GroupChatroomIdAndQuitFalse(memberId, roomId);
    }

    public GroupChat saveChat(GroupChat groupChat) {
        return groupChatRepository.save(groupChat);
    }
}
