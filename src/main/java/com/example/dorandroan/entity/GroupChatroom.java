package com.example.dorandroan.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@Table(name = "GROUP_CHATROOM")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupChatroom {

    @Id
    @Column(name = "group_chatroom_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupChatroomId;

    @Column
    private String title;

    @Column
    private Integer maxPartIn;

    @Column
    private String chatRoomImg;

    @Column
    private String description;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean closed;
}
