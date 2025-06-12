package com.example.dorandroan.service;

import com.example.dorandroan.entity.Member;
import com.example.dorandroan.entity.PrivateChatroom;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.ChattingErrorCode;
import com.example.dorandroan.repository.MemberChatRoomRepository;
import com.example.dorandroan.repository.PrivateChatroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatAndMemberUtil {
    private final PrivateChatroomRepository privateChatroomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;

    public Member findOtherAtPrivateChatroom(Long roomId, Member nowMember) {
        PrivateChatroom privateChatroom = privateChatroomRepository.findById(roomId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND));
        if (privateChatroom.getMemberA().equals(nowMember) && !privateChatroom.isBOut())
            return privateChatroom.getMemberB();
        else if (privateChatroom.getMemberB().equals(nowMember) && !privateChatroom.isAOut())
            return privateChatroom.getMemberA();
        throw new RestApiException(ChattingErrorCode.NOT_PART_IN);
    }

    public List<Member> findGroupChatroomMembers(Long roomId) {
        return memberChatRoomRepository.findMemberByChatRoom(roomId);
    }

    public boolean amIMemberA(PrivateChatroom chatroom, Member nowMember) {
        return chatroom.getMemberA().equals(nowMember);
    }
}
