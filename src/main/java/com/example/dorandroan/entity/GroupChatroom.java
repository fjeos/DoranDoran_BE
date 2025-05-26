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
    private Integer nowPartIn;

    @Column
    private String chatRoomImg;

    @Column
    private String description;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean closed;

    public void enterRoom() {
        if (this.nowPartIn < this.maxPartIn)
            this.nowPartIn++;
    }

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeRoomImage(String chatRoomImage) {
        this.chatRoomImg = chatRoomImage;
    }

    public void changeMaxCount(Integer maxCount) {
        this.maxPartIn = maxCount;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void delete() {
        this.closed = true;
    }
}
