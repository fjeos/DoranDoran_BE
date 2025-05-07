package com.example.dorandroan.dto;

import com.example.dorandroan.entity.GroupChat;
import com.example.dorandroan.entity.GroupChatroom;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class ChatRoomListResponseDto {
    private Long chatRoomId;
    private String chatRoomTitle;
    private int partInPeople;
    private String chatRoomImage;
    private int nonReadCount;
    private String lastChatContent;
    private String lastChatTime;

    public static ChatRoomListResponseDto toDto(GroupChatroom chatRoom, GroupChat lastChat) {
        return ChatRoomListResponseDto.builder()
                .chatRoomId(chatRoom.getGroupChatroomId())
                .chatRoomTitle(chatRoom.getTitle())
                .partInPeople(chatRoom.getMaxPartIn())
                .chatRoomImage(chatRoom.getChatRoomImg())
                .nonReadCount(0)
                .lastChatContent(lastChat.getContent())
                .lastChatTime(changeDateToString(lastChat))
                .build();
    }
    private static String changeDateToString(GroupChat chat) {
        if (chat.getSendAt().toLocalDate().isEqual(LocalDate.now())) {
            return chat.getSendAt().format(DateTimeFormatter.ofPattern("a hh:mm"));
        } else {
            return chat.getSendAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }
}
