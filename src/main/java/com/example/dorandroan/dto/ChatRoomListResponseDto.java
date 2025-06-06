package com.example.dorandroan.dto;

import com.example.dorandroan.entity.Chat;
import com.example.dorandroan.entity.GroupChatroom;
import com.example.dorandroan.global.ConvertDateUtil;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomListResponseDto {

    private Long chatRoomId;
    private String chatRoomTitle;
    private String chatRoomImage;
    private Integer maxCount;
    private Integer partInPeople;
    private String description;
    private String lastChatTime;

    public static ChatRoomListResponseDto toDto(GroupChatroom gc, Chat lastChat) {
        return ChatRoomListResponseDto.builder()
                .chatRoomId(gc.getGroupChatroomId())
                .chatRoomTitle(gc.getTitle())
                .chatRoomImage(gc.getChatRoomImg())
                .maxCount(gc.getMaxPartIn())
                .partInPeople(gc.getNowPartIn())
                .description(gc.getDescription())
                .lastChatTime(ConvertDateUtil.getLastChatTime(lastChat.getSendAt())).build();
    }
}
