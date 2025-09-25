package com.example.dorandroan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Builder
@Table(name = "MEMBER_CHATROOM")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberChatroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberChatroomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChatRoomRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_chatroom_id")
    private GroupChatroom groupChatroom;

    @Column
    private Instant joinTime;

    @Column
    private Instant enterTime;

    @Column
    private Instant leaveTime;

    public void enter() {
        this.enterTime = Instant.now();
    }

    public void leave() {
        this.leaveTime = Instant.now();
    }
}
