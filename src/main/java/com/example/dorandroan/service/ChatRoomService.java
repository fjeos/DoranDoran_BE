package com.example.dorandroan.service;

import com.example.dorandroan.dto.*;
import com.example.dorandroan.entity.*;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.ChattingErrorCode;
import com.example.dorandroan.global.error.MemberErrorCode;
import com.example.dorandroan.global.jwt.CustomUserDetails;
import com.example.dorandroan.global.jwt.CustomUserDetailsService;
import com.example.dorandroan.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    private Member getNowMember(Long memberId) {
        return memberService.findMember(memberId);
    }

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
        memberChatRoomRepository.save(MemberChatroom.builder().member(member)
                .role(ChatRoomRole.LEAD)
                .quit(false)
                .groupChatroom(newChatRoom).build());
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


    public List<ChatRoomMembersResponseDto> getGroupChatRoomMembers(Member member, Long chatRoomId) {

        List<Member> memberByChatRoom = memberChatRoomRepository.findMemberByChatRoom(chatRoomId);
        if (memberByChatRoom.isEmpty())
            throw new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND);

        if (memberByChatRoom.stream().noneMatch(m -> m.getMemberId().equals(member.getMemberId())))
            throw new RestApiException(ChattingErrorCode.NOT_PART_IN);

        return memberByChatRoom.stream()
                .map(ChatRoomMembersResponseDto::toDto).toList();
    }

    public List<ChatRoomMembersResponseDto> getPrivateChatRoomMembers(Long memberId, Long privateChatRoomId) {
        Member nowMember = getNowMember(memberId);
        PrivateChatroom privateChatroom = privateChatroomRepository.findById(privateChatRoomId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND));

        if (!(nowMember.equals(privateChatroom.getMemberA()) || nowMember.equals(privateChatroom.getMemberB())))
            throw new RestApiException(ChattingErrorCode.NOT_PART_IN);

        return List.of(ChatRoomMembersResponseDto.toDto(privateChatroom.getMemberA()),
                ChatRoomMembersResponseDto.toDto(privateChatroom.getMemberB()));
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
    public Long createPrivateChatroom(Member member, Long otherMemberId) {
        Member nowMember = getNowMember(member.getMemberId());
        Member otherMember = memberService.findMember(otherMemberId);
        if (nowMember.equals(otherMember) || nowMember.getState() || otherMember.getState())
            throw new RestApiException(ChattingErrorCode.INVALID_MEMBER);

        return privateChatroomRepository.save(
                PrivateChatroom.builder()
                .memberA(nowMember)
                .memberB(otherMember).build()).getPrivateChatroomId();
    }

    @Transactional
    public void enterGroupChatroom(CustomUserDetails member, Long chatRoomId) {

        GroupChatroom groupChatroom = groupChatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND));

        System.out.println("Now Member  " + member.getMember().getMemberId());
        if (memberChatRoomRepository.existsByMember_MemberIdAndGroupChatroom_GroupChatroomId(
                member.getMember().getMemberId(), chatRoomId))
            throw new RestApiException(ChattingErrorCode.ALREADY_JOINED);

        memberChatRoomRepository.save(MemberChatroom.builder()
                .groupChatroom(groupChatroom)
                .member(memberService.findMember(member.getMember().getMemberId()))
                .role(ChatRoomRole.PART).build());
    }
}
