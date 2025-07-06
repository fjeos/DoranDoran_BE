package com.example.dorandroan.service;

import com.example.dorandroan.dto.ChatRoomMembersResponseDto;
import com.example.dorandroan.entity.Member;
import com.example.dorandroan.entity.PrivateChat;
import com.example.dorandroan.entity.PrivateChatroom;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.ChattingErrorCode;
import com.example.dorandroan.repository.PrivateChatRepository;
import com.example.dorandroan.repository.PrivateChatroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateChatroomService {

    private final PrivateChatroomRepository privateChatroomRepository;
    private final PrivateChatRepository privateChatRepository;
    private final ChatAndMemberUtil chatAndMemberUtil;
    private final MemberService memberService;

    public List<ChatRoomMembersResponseDto> getPrivateChatRoomMembers(Long memberId, Long privateChatRoomId) {
        Member nowMember = memberService.findMember(memberId);
        PrivateChatroom privateChatroom = privateChatroomRepository.findById(privateChatRoomId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND));

        if (!(nowMember.equals(privateChatroom.getMemberA()) || nowMember.equals(privateChatroom.getMemberB())))
            throw new RestApiException(ChattingErrorCode.NOT_PART_IN);

        return List.of(ChatRoomMembersResponseDto.toDto(privateChatroom.getMemberA()),
                ChatRoomMembersResponseDto.toDto(privateChatroom.getMemberB()));
    }

    @Transactional
    public Long createPrivateChatroom(Member member, Long otherMemberId) {
        Member nowMember = memberService.findMember(member.getMemberId());
        Member otherMember = memberService.findMember(otherMemberId);
        if (nowMember.equals(otherMember) || nowMember.getState() || otherMember.getState() || !otherMember.getRecommends())
            throw new RestApiException(ChattingErrorCode.INVALID_MEMBER);

        if (privateChatroomRepository.findByTwoMembers(nowMember, otherMember).isPresent()) {
            throw new RestApiException(ChattingErrorCode.ALREADY_JOINED);
        }

        return privateChatroomRepository.save(
                PrivateChatroom.builder()
                        .memberA(nowMember)
                        .memberB(otherMember).build()).getPrivateChatroomId();
    }

    @Transactional
    public void outOfPrivateChatRoom(Member member, Long privateId) {
        PrivateChatroom chatroom = privateChatroomRepository.findChatroomByMemberAndNotQuit(member, privateId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.NOT_PART_IN));

        boolean isA = chatroom.getMemberA().equals(member);
        if (isA ? chatroom.isAOut() : chatroom.isBOut())
            throw new RestApiException(ChattingErrorCode.NOT_PART_IN);
        if (isA) chatroom.outA();
        else chatroom.outB();

    }

    public int countPrivateUnreadChat(Member nowMember, PrivateChatroom privateChatroom) {
        if (chatAndMemberUtil.amIMemberA(privateChatroom, nowMember))
            return privateChatRepository.countByChatRoomIdAndSendAtAfter(privateChatroom.getPrivateChatroomId(),
                    privateChatroom.getALeaveTime());
        else
            return privateChatRepository.countByChatRoomIdAndSendAtAfter(privateChatroom.getPrivateChatroomId(), privateChatroom.getBLeaveTime());
    }

    public List<PrivateChatroom> findChatroomByMember(Member member) {
        return privateChatroomRepository.findChatroomByMember(member);
    }

    public PrivateChat findTop(Long chatroomId) {
        return privateChatRepository.findTopByChatRoomIdOrderBySendAtDesc(chatroomId);
    }

    public Optional<PrivateChatroom> validateChattingMember(Member member, Long roomId) {
        return privateChatroomRepository.findChatroomByMemberAndNotQuit(member, roomId);
    }

    public PrivateChat saveChat(PrivateChat privateChat) {
        return privateChatRepository.save(privateChat);
    }

    public PrivateChatroom findById(Long roomId) {
        return privateChatroomRepository.findById(roomId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND));
    }
}
