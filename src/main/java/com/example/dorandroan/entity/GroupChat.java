package com.example.dorandroan.entity;

import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "GroupChat")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupChat extends Chat {

    @Id
    private ObjectId id;
    private String groupChatId;

    @Override
    public ObjectId getChatId() {
        return id;
    }
}
