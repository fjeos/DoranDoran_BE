package com.example.dorandroan.entity;

import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "PrivateChat")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrivateChat extends Chat {

    @Id
    private ObjectId id;
    private String privateChatId;

    @Override
    public ObjectId getChatId() {
        return id;
    }
}
