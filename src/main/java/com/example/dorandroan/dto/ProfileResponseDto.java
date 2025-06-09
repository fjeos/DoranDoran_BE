package com.example.dorandroan.dto;

import com.example.dorandroan.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileResponseDto {

    private Long memberId;
    private String profileImage;
    private String nickname;
    private Boolean chatPermitted;

    public static ProfileResponseDto toDto(Member member) {
        return ProfileResponseDto.builder()
                .memberId(member.getMemberId())
                .profileImage(member.getProfileImg())
                .nickname(member.getNickname())
                .chatPermitted(member.getRecommends()).build();
    }
}
