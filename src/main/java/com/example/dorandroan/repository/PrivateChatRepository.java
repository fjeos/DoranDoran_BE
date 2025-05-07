package com.example.dorandroan.repository;

import com.example.dorandroan.entity.GroupChat;
import com.example.dorandroan.entity.PrivateChat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivateChatRepository extends MongoRepository<PrivateChat, String> {
    Optional<PrivateChat> findBySenderId(Long senderId);

    PrivateChat findTopByChatRoomIdOrderBySendAtDesc(Long chatRoomId);
}
