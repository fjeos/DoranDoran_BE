package com.example.dorandroan.dto;

import com.example.dorandroan.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomMembersResponseDto {

    private Long memberId;
    private String profileImage;
    private String nickname;

    public static ChatRoomMembersResponseDto toDto(Member member) {
        return ChatRoomMembersResponseDto.builder()
                .memberId(member.getMemberId())
                .profileImage(member.getProfileImg())
                .nickname(member.getNickname())
                .build();
    }

}
