package com.example.dorandroan.dto;

import com.example.dorandroan.entity.GroupChatroom;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatroomInfoResponseDto {

    private String chatRoomTitle;
    private String chatRoomImage;
    private int maxCount;
    private int currentCount;
    private String description;
    @JsonProperty("isManager")
    private boolean manager;

    public static ChatroomInfoResponseDto toDto(GroupChatroom chatroom, boolean managerMember) {
        return ChatroomInfoResponseDto.builder()
                .chatRoomTitle(chatroom.getTitle())
                .chatRoomImage(chatroom.getChatRoomImg())
                .maxCount(chatroom.getMaxPartIn())
                .currentCount(chatroom.getNowPartIn())
                .description(chatroom.getDescription())
                .manager(managerMember).build();
    }
}
