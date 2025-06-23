package com.example.dorandroan.dto;

import com.example.dorandroan.entity.*;
import com.example.dorandroan.global.ConvertDateUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class MyChatRoomListResponseDto {
    private Long chatRoomId;
    @JsonProperty("isGroup")
    private boolean group;
    private String chatRoomTitle;
    private int partInPeople;
    private String chatRoomImage;
    @JsonProperty("isClosed")
    private boolean closed;
    private int nonReadCount;
    private String lastChatContent;
    @JsonIgnore
    private Instant lastChatTime;

    public static MyChatRoomListResponseDto toDto(GroupChatroom chatRoom, Chat lastChat, int unreadCount) {
        boolean isChatNull = lastChat == null;
        return MyChatRoomListResponseDto.builder()
                .chatRoomId(chatRoom.getGroupChatroomId())
                .group(true)
                .chatRoomTitle(chatRoom.getTitle())
                .partInPeople(chatRoom.getNowPartIn())
                .chatRoomImage(chatRoom.getChatRoomImg())
                .closed(chatRoom.isClosed())
                .nonReadCount(unreadCount)
                .lastChatContent(isChatNull? null : lastChat.getContent())
                .lastChatTime(isChatNull? null : lastChat.getSendAt())
                .build();
    }
    public static MyChatRoomListResponseDto toPrivateDto(PrivateChatroom chatRoom, Chat lastChat, Member otherMember,
                                                         int unreadCount) {
        boolean isChatNull = lastChat == null;
        return MyChatRoomListResponseDto.builder()
                .chatRoomId(chatRoom.getPrivateChatroomId())
                .group(false)
                .chatRoomTitle(otherMember.getNickname())
                .partInPeople(2)
                .chatRoomImage(otherMember.getProfileImg())
                .closed(false)
                .nonReadCount(unreadCount)
                .lastChatContent(isChatNull? null : lastChat.getContent())
                .lastChatTime(isChatNull? null : lastChat.getSendAt())
                .build();
    }

    @JsonProperty("lastChatTime")
    public String getLastChatTime() {
        return ConvertDateUtil.getLastChatTime(lastChatTime);
    }
}
