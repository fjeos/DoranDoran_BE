package com.example.dorandroan.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Getter
public class ChatRoomRequestDto {

    @NotBlank(message = "제목은 공백일 수 없습니다.")
    @Size(min=1, max=15, message = "제목은 1자 이상 15자 이하입니다.")
    private String chatRoomTitle;

    private String chatRoomImage;

    @Range(min=3, max=100, message = "채팅방 인원은 3명 이상 100명 이하입니다.")
    private Integer maxCount;

    @Size(max=255, message = "설명은 최대 255자까지 작성할 수 있습니다.")
    private String description;
}
