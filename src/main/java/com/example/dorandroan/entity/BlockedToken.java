package com.example.dorandroan.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@Getter
@Builder
@RedisHash(value = "block")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlockedToken implements Serializable {

    @Id
    private Long memberId;

    @Indexed
    private String token;

    @TimeToLive
    private Long ttl;
}