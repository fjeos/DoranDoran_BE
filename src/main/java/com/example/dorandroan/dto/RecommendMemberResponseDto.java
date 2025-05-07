package com.example.dorandroan.dto;

import com.example.dorandroan.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendMemberResponseDto {

    private Long memberId;
    private String profileImg;
    private String nickname;

    public static RecommendMemberResponseDto toDto(Member member) {
        return RecommendMemberResponseDto.builder()
                .memberId(member.getMemberId())
                .profileImg(member.getProfileImg())
                .nickname(member.getNickname())
                .build();
    }
}
