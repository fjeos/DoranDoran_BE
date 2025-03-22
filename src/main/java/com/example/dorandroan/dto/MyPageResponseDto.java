package com.example.dorandroan.dto;

import com.example.dorandroan.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MyPageResponseDto {

    private String email;
    private Boolean isPermitted;
    private Boolean isNotification;

    public static MyPageResponseDto toDto(Member member) {
        return MyPageResponseDto.builder()
                .email(member.getEmail())
                .isPermitted(member.getRecommends())
                .isNotification(member.getPush()).build();
    }
}
