package com.example.dorandroan.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@Table(name = "CHATROOM")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @Column(name = "chatroom_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatroomId;

    @Column
    private String title;

    @Column
    private Integer maxPartIn;

    @Column
    private String chatRoomImg;

    @Column
    private String description;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean closed;
}
