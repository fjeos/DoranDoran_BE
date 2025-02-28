package com.example.dorandroan.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@Table(name = "MEMBER")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String nickname;

    @Column
    private String profileImg;

    @Column
    private Boolean state;

    @Column
    private Boolean recommends;

    @Column
    private String refreshToken;

    @Column
    private String deviceToken;

    @Column
    private Boolean push;

    @Column
    private Role role;

    public void publishToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
