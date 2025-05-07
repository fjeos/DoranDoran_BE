package com.example.dorandroan.repository;

import com.example.dorandroan.entity.GroupChatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GroupChatRoomRepository extends JpaRepository<GroupChatroom, Long> {

    @Query("SELECT gc FROM GroupChatroom gc WHERE gc.groupChatroomId = :chatroomId and gc.closed = false")
    Optional<GroupChatroom> findById(Long chatroomId);
}
