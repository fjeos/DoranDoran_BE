package com.example.dorandroan.repository;

import com.example.dorandroan.entity.GroupChat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupChatRepository extends MongoRepository<GroupChat, String> {
    Optional<GroupChat> findBySenderId(Long senderId);

    GroupChat findTopByChatRoomIdOrderBySendAtDesc(Long chatRoomId);
}
