package com.example.dorandroan.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Builder
@RedisHash(value = "code", timeToLive = 3600)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthCode implements Serializable {

    @Id
    private String email;

    private Integer authCode;

    @Builder.Default
    private Boolean approved = false;

    public void updateApproved() {
        this.approved = true;
    }
}
