package com.example.dorandroan.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@Table(name = "PRIVATE_CHATROOM")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrivateChatroom {

    @Id
    @Column(name = "private_chatroom_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long privateChatroomId;

    @Column
    private Long aId;

    @Column
    private Long bId;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean aOut;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean bOut;

}
