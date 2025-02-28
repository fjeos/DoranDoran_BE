package com.example.dorandroan.dto;

import com.example.dorandroan.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberLoginResponseDto {

    private Long memberId;

    private String nickname;

    private String profileImage;

    private String accessToken;

    public static MemberLoginResponseDto toDto(Member member, String accessToken) {
        return MemberLoginResponseDto.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImg())
                .accessToken(accessToken).build();
    }
}
