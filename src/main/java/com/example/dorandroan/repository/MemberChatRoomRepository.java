package com.example.dorandroan.repository;

import com.example.dorandroan.entity.ChatRoom;
import com.example.dorandroan.entity.Member;
import com.example.dorandroan.entity.MemberChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberChatRoomRepository extends JpaRepository<MemberChatRoom, Long> {

    @Query("SELECT mc.chatRoom FROM MemberChatRoom mc WHERE mc.member = :member")
    List<ChatRoom> findChatRoomByMember(Member member);

    @Query("SELECT mc.member FROM MemberChatRoom mc WHERE mc.chatRoom.chatroomId = :chatRoomId")
    List<Member> findMemberByChatRoom(Long chatRoomId);
}
