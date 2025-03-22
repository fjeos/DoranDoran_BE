package com.example.dorandroan.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    private String deviceToken;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean push;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

}
