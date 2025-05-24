package com.example.dorandroan.repository;

import com.example.dorandroan.entity.GroupChatroom;
import com.example.dorandroan.entity.Member;
import com.example.dorandroan.entity.MemberChatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

public interface MemberChatRoomRepository extends JpaRepository<MemberChatroom, Long> {

    @Query("SELECT mc.groupChatroom FROM MemberChatroom mc WHERE mc.member = :member")
    List<GroupChatroom> findChatRoomByMember(Member member);

    @Query("SELECT mc.member FROM MemberChatroom mc WHERE mc.groupChatroom.groupChatroomId = :chatRoomId")
    List<Member> findMemberByChatRoom(Long chatRoomId);

    boolean existsByMember_MemberIdAndGroupChatroom_GroupChatroomId(Long memberId, Long chatRoomId);

    @Query("SELECT mc FROM MemberChatroom  mc WHERE  mc.member = :member and mc.groupChatroom.groupChatroomId = :chatRoomId")
    Optional<MemberChatroom> findChatRoomByMemberAndChatRoomId(Member member, Long chatRoomId);
}
