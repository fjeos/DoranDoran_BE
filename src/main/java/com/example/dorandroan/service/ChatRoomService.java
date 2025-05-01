package com.example.dorandroan.service;

import com.example.dorandroan.dto.ChatRoomListResponseDto;
import com.example.dorandroan.dto.ChatRoomMembersResponseDto;
import com.example.dorandroan.dto.ChatRoomRequestDto;
import com.example.dorandroan.dto.ProfileResponseDto;
import com.example.dorandroan.entity.*;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.ChattingErrorCode;
import com.example.dorandroan.global.jwt.CustomUserDetails;
import com.example.dorandroan.repository.ChatRepository;
import com.example.dorandroan.repository.ChatRoomRepository;
import com.example.dorandroan.repository.MemberChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final ChatRepository chatRepository;
    private final MemberService memberService;

    @Transactional
    public Long createChatRoom(Member member, ChatRoomRequestDto requestDto) {

        ChatRoom newChatRoom = ChatRoom.builder().
                title(requestDto.getChatRoomTitle())
                .chatRoomImg(requestDto.getChatRoomImage())
                .description(requestDto.getDescription())
                .maxPartIn(requestDto.getMaxCount())
                .closed(false)
                .build();
        chatRoomRepository.save(newChatRoom);
        MemberChatRoom.builder().member(member)
                .role(ChatRoomRole.LEAD)
                .quit(false)
                .chatRoom(newChatRoom);
        return newChatRoom.getChatroomId();
    }

    public List<ChatRoomListResponseDto> getChatRoomLists(CustomUserDetails member) {

        List<ChatRoom> chatRoomList = memberChatRoomRepository.findChatRoomByMember(member.getMember());
        if (chatRoomList.isEmpty())
            throw new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND);

        return  chatRoomList.stream().map(
                c -> ChatRoomListResponseDto.toDto(c,
                        chatRepository.findTopByChatRoomIdOrderBySendAtDesc(c.getChatroomId()))
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

        return ProfileResponseDto.toDto(memberService.findMember(memberId));
    }
}
