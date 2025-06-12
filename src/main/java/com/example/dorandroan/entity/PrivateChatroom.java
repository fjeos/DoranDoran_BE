package com.example.dorandroan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "a_member_id")
    private Member memberA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b_member_id")
    private Member memberB;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean aOut;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean bOut;

    @Column
    private Instant aEnterTime;

    @Column
    private Instant bEnterTime;

    @Column
    private Instant aLeaveTime;

    @Column
    private Instant bLeaveTime;

    public void outA() {
        this.aOut = true;
    }

    public void outB() {
        this.bOut = true;
    }

    public void enterA() {
        this.aEnterTime = Instant.now();
    }

    public void enterB() {
        this.bEnterTime = Instant.now();
    }

    public void leaveA() {
        this.aLeaveTime = Instant.now();
    }

    public void leaveB() {
        this.bLeaveTime = Instant.now();
    }

}
