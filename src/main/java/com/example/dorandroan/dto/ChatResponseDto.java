package com.example.dorandroan.dto;

import com.example.dorandroan.entity.Chat;
import com.example.dorandroan.entity.GroupChat;
import com.example.dorandroan.entity.Member;
import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Getter
@Builder
public class ChatResponseDto {

    private String chatId;
    private String type;
    private String content;
    private Long senderId;
    private String senderNickname;
    private String senderProfileImage;
    private String date;
    private String time;

    public static ChatResponseDto toDto(Chat chat, Member member) {
        return ChatResponseDto.builder()
                .chatId(chat.getChatId().toHexString())
                .type(chat.getType())
                .content(chat.getContent())
                .senderId(member.getMemberId())
                .senderNickname(member.getNickname())
                .senderProfileImage(member.getProfileImg())
                .date(chat.getSendAt().atZone(ZoneId.of("Asia/Seoul"))
                        .format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
                .time(chat.getSendAt().atZone(ZoneId.of("Asia/Seoul"))
                        .format(DateTimeFormatter.ofPattern("a hh:mm").withLocale(Locale.KOREAN)))
                .build();
    }
}
