package com.example.dorandroan.repository;

import com.example.dorandroan.entity.Member;
import com.example.dorandroan.entity.PrivateChatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PrivateChatroomRepository extends JpaRepository<PrivateChatroom, Long> {

    @Query("SELECT cr FROM PrivateChatroom cr WHERE cr.memberA = :member or cr.memberB = :member")
    List<PrivateChatroom> findChatroomByMember(Member member);

    @Query("SELECT cr FROM PrivateChatroom cr WHERE cr.privateChatroomId = :privateId and " +
            "(cr.memberA = :member and cr.aOut = false ) or " +
            "(cr.memberB = :member and cr.bOut = false)")
    Optional<PrivateChatroom> findChatroomByMemberAndNotQuit(Member member, Long privateId);

    @Query("SELECT pc FROM PrivateChatroom pc WHERE pc.memberA = :nowMember and pc.memberB = :otherMember or " +
            "pc.memberA = :otherMember and pc.memberB = :nowMember")
    Optional<PrivateChatroom> findByTwoMembers(Member nowMember, Member otherMember);
}
