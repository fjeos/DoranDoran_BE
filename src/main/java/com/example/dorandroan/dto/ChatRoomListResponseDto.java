package com.example.dorandroan.dto;

import com.example.dorandroan.entity.Chat;
import com.example.dorandroan.entity.ChatRoom;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public static ChatRoomListResponseDto toDto(ChatRoom chatRoom, Chat lastChat) {
        return ChatRoomListResponseDto.builder()
                .chatRoomId(chatRoom.getChatroomId())
                .chatRoomTitle(chatRoom.getTitle())
                .partInPeople(chatRoom.getMaxPartIn())
                .chatRoomImage(chatRoom.getChatRoomImg())
                .nonReadCount(0)
                .lastChatContent(lastChat.getContent())
                .lastChatTime(changeDateToString(lastChat))
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
