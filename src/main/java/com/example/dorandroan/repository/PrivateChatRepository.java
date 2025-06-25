package com.example.dorandroan.repository;

import com.example.dorandroan.entity.PrivateChat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface PrivateChatRepository extends MongoRepository<PrivateChat, String> {
    PrivateChat findTopByChatRoomIdOrderBySendAtDesc(Long chatRoomId);

    int countBySendAtBetween(Instant leaveTime, Instant enterTime);
}
