package com.example.dorandroan.repository;

import com.example.dorandroan.entity.GroupChatroom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupChatRoomRepository extends JpaRepository<GroupChatroom, Long> {

    @Query("SELECT gc FROM GroupChatroom gc WHERE gc.groupChatroomId = :chatroomId and gc.closed = false")
    Optional<GroupChatroom> findById(Long chatroomId);

    @Query("SELECT gc FROM GroupChatroom  gc WHERE :cursor IS NULL OR gc.groupChatroomId >= :cursor and gc.closed = false")
    List<GroupChatroom> findAllAndNotClosed(@Param("cursor") Long cursor, Pageable pageable);
}
