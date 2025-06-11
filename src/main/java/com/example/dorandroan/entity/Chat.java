package com.example.dorandroan.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Chat {
    Long chatRoomId;
    Long senderId;
    String content;
    MessageType type;
    Instant sendAt;

    public abstract ObjectId getChatId();
}
