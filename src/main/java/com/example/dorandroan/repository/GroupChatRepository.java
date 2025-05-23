package com.example.dorandroan.repository;

import com.example.dorandroan.dto.ChatResponseDto;
import com.example.dorandroan.entity.Chat;
import com.example.dorandroan.entity.GroupChat;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupChatRepository extends MongoRepository<GroupChat, String> {
    Optional<GroupChat> findBySenderId(Long senderId);

    GroupChat findTopByChatRoomIdOrderBySendAtDesc(Long chatRoomId);

    List<Chat> findByChatRoomIdAndIdLessThanOrderByIdDesc(Long chatRoomId, ObjectId lastId, Pageable pageable);

    List<Chat> findByChatRoomIdOrderByIdDesc(Long chatRoomId, Pageable pageable);
}
