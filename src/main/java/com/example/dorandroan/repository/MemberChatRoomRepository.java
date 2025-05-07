package com.example.dorandroan.repository;

import com.example.dorandroan.entity.GroupChatroom;
import com.example.dorandroan.entity.Member;
import com.example.dorandroan.entity.MemberChatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberChatRoomRepository extends JpaRepository<MemberChatroom, Long> {

    @Query("SELECT mc.groupChatroom FROM MemberChatroom mc WHERE mc.member = :member")
    List<GroupChatroom> findChatRoomByMember(Member member);

    @Query("SELECT mc.member FROM MemberChatroom mc WHERE mc.groupChatroom.groupChatroomId = :chatRoomId")
    List<Member> findMemberByChatRoom(Long chatRoomId);
}
