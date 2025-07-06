package com.example.dorandroan.service;

import com.example.dorandroan.dto.ChatRoomListResponseDto;
import com.example.dorandroan.dto.ChatRoomMembersResponseDto;
import com.example.dorandroan.dto.ChatroomInfoResponseDto;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupChatroomService {

    private final GroupChatRoomRepository groupChatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final GroupChatRepository groupChatRepository;

    public List<ChatRoomMembersResponseDto> getGroupChatRoomMembers(Member member, Long chatRoomId) {

        List<Member> memberByChatRoom = memberChatRoomRepository.findMemberByChatRoom(chatRoomId);
        if (memberByChatRoom.isEmpty())
            throw new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND);

        if (memberByChatRoom.stream().noneMatch(m -> m.getMemberId().equals(member.getMemberId())))
            throw new RestApiException(ChattingErrorCode.NOT_PART_IN);

        return memberByChatRoom.stream()
                .map(ChatRoomMembersResponseDto::toDto).toList();
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

    public GroupChatroom findChatroom(Long roomId) {
        return groupChatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND));
    }

}
