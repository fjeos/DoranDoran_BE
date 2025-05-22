package com.example.dorandroan.dto;

import com.example.dorandroan.entity.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
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
    private int nonReadCount;
    private String lastChatContent;
    private String lastChatTime;

    public static ChatRoomListResponseDto toDto(GroupChatroom chatRoom, Chat lastChat) {
        boolean isChatNull = lastChat == null;
        return ChatRoomListResponseDto.builder()
                .chatRoomId(chatRoom.getGroupChatroomId())
                .group(true)
                .chatRoomTitle(chatRoom.getTitle())
                .partInPeople(chatRoom.getMaxPartIn())
                .chatRoomImage(chatRoom.getChatRoomImg())
                .nonReadCount(isChatNull? 0 : 1)
                .lastChatContent(isChatNull? null : lastChat.getContent())
                .lastChatTime(isChatNull? null : changeDateToString(lastChat))
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
                .nonReadCount(isChatNull? 0 : 1)
                .lastChatContent(isChatNull? null : lastChat.getContent())
                .lastChatTime(isChatNull? null : changeDateToString(lastChat))
                .build();
    }
    private static String changeDateToString(Chat chat) {
        if (chat.getSendAt().toLocalDate().isEqual(LocalDate.now())) {
            return chat.getSendAt().format(DateTimeFormatter.ofPattern("a hh:mm"));
        } else {
            return chat.getSendAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }
}
