package com.example.dorandroan.dto;

import com.example.dorandroan.entity.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class ChatRoomListResponseDto {
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
    private LocalDateTime lastChatTime;

    public static ChatRoomListResponseDto toDto(GroupChatroom chatRoom, Chat lastChat) {
        boolean isChatNull = lastChat == null;
        return ChatRoomListResponseDto.builder()
                .chatRoomId(chatRoom.getGroupChatroomId())
                .group(true)
                .chatRoomTitle(chatRoom.getTitle())
                .partInPeople(chatRoom.getMaxPartIn())
                .chatRoomImage(chatRoom.getChatRoomImg())
                .closed(chatRoom.isClosed())
                .nonReadCount(isChatNull? 0 : 1)
                .lastChatContent(isChatNull? null : lastChat.getContent())
                .lastChatTime(isChatNull? null : lastChat.getSendAt())
                .build();
    }
    public static ChatRoomListResponseDto toPrivateDto(PrivateChatroom chatRoom, Chat lastChat, Member otherMember) {
        boolean isChatNull = lastChat == null;
        return ChatRoomListResponseDto.builder()
                .chatRoomId(chatRoom.getPrivateChatroomId())
                .group(false)
                .chatRoomTitle(otherMember.getNickname())
                .partInPeople(2)
                .chatRoomImage(otherMember.getProfileImg())
                .closed(false)
                .nonReadCount(isChatNull? 0 : 1)
                .lastChatContent(isChatNull? null : lastChat.getContent())
                .lastChatTime(isChatNull? null : lastChat.getSendAt())
                .build();
    }

    @JsonProperty("lastChatTime")
    public String getLastChatTime() {
        if (lastChatTime.toLocalDate().isEqual(LocalDate.now())) {
            return lastChatTime.atZone(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("a hh:mm"));
        } else {
            return lastChatTime.atZone(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }
}
