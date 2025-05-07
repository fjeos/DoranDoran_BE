package com.example.dorandroan.service;

import com.example.dorandroan.dto.*;
import com.example.dorandroan.entity.*;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.ChattingErrorCode;
import com.example.dorandroan.global.error.MemberErrorCode;
import com.example.dorandroan.global.jwt.CustomUserDetails;
import com.example.dorandroan.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final GroupChatRoomRepository groupChatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final PrivateChatroomRepository privateChatroomRepository;
    private final GroupChatRepository chatRepository;
    private final MemberService memberService;

    @Transactional
    public Long createGroupChatroom(Member member, ChatRoomRequestDto requestDto) {

        GroupChatroom newChatRoom = GroupChatroom.builder().
                title(requestDto.getChatRoomTitle())
                .chatRoomImg(requestDto.getChatRoomImage())
                .description(requestDto.getDescription())
                .maxPartIn(requestDto.getMaxCount())
                .closed(false)
                .build();
        groupChatRoomRepository.save(newChatRoom);
        MemberChatroom.builder().member(member)
                .role(ChatRoomRole.LEAD)
                .quit(false)
                .groupChatroom(newChatRoom);
        return newChatRoom.getGroupChatroomId();
    }

    public List<ChatRoomListResponseDto> getChatRoomLists(CustomUserDetails member) {

        List<GroupChatroom> chatRoomList = memberChatRoomRepository.findChatRoomByMember(member.getMember());
        if (chatRoomList.isEmpty())
            throw new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND);

        return  chatRoomList.stream().map(
                c -> ChatRoomListResponseDto.toDto(c,
                        chatRepository.findTopByChatRoomIdOrderBySendAtDesc(c.getGroupChatroomId()))
        ).collect(Collectors.toList());
    }


    public List<ChatRoomMembersResponseDto> getChatRoomMembers(Long chatRoomId) {

        List<ChatRoomMembersResponseDto> result = memberChatRoomRepository.findMemberByChatRoom(chatRoomId).stream()
                .map(ChatRoomMembersResponseDto::toDto).toList();
        if (result.isEmpty())
            throw new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND);

        return result;
    }

    public ProfileResponseDto getMemberProfile(Long memberId) {

        Member findMember = memberService.findMember(memberId);
        if (findMember.getState())
            throw new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND);
        return ProfileResponseDto.toDto(findMember);
    }

    public List<RecommendMemberResponseDto> getRecommendMembers() {

        return memberService.getRecommendMembers().stream()
                .map(RecommendMemberResponseDto::toDto).collect(Collectors.toList());
    }

    @Transactional
    public Long createPrivateChatroom(CustomUserDetails member, Long memberId) {
        if (member.getMember().getMemberId().equals(memberId) || memberService.findMember(memberId).getState())
            throw new RestApiException(ChattingErrorCode.INVALID_MEMBER);

        return privateChatroomRepository.save(
                PrivateChatroom.builder()
                .aId(member.getMember().getMemberId())
                .bId(memberId).build()).getPrivateChatroomId();
    }
}
