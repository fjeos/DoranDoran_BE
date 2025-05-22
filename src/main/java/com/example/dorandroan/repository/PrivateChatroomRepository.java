package com.example.dorandroan.repository;

import com.example.dorandroan.entity.Member;
import com.example.dorandroan.entity.PrivateChatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PrivateChatroomRepository extends JpaRepository<PrivateChatroom, Long> {

    @Query("SELECT cr FROM PrivateChatroom cr WHERE cr.memberA = :member or cr.memberB = :member")
    List<PrivateChatroom> findChatroomByMember(Member member);
}
