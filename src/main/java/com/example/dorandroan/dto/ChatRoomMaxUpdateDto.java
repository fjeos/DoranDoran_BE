package com.example.dorandroan.dto;

import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Getter
public class ChatRoomMaxUpdateDto {

    private Long chatRoomId;

    @Range(min=3, max=100)
    private Integer maxCount;
}
