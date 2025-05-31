package com.example.dorandroan.repository;

import com.example.dorandroan.entity.Chat;
import com.example.dorandroan.entity.PrivateChat;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrivateChatRepository extends MongoRepository<PrivateChat, String> {
    Optional<PrivateChat> findBySenderId(Long senderId);

    PrivateChat findTopByChatRoomIdOrderBySendAtDesc(Long chatRoomId);

    List<Chat> findByChatRoomIdAndIdLessThanOrderById(Long chatRoomId, ObjectId lastId, Pageable pageable);

    List<Chat> findByChatRoomIdOrderById(Long chatRoomId, Pageable pageable);

}
