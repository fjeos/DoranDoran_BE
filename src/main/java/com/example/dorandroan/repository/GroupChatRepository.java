package com.example.dorandroan.repository;

import com.example.dorandroan.entity.GroupChat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface GroupChatRepository extends MongoRepository<GroupChat, String> {
    GroupChat findTopByChatRoomIdOrderBySendAtDesc(Long chatRoomId);

    int countByChatRoomIdAndSendAtAfter(Long chatRoomId, Instant leaveTime);
}
