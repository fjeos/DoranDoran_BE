package com.example.dorandroan.repository;

import com.example.dorandroan.entity.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {
    Optional<Chat> findBySenderId(Long senderId);

    Chat findTopByChatRoomIdOrderBySendAtDesc(Long chatRoomId);
}
