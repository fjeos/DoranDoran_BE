package com.example.dorandroan.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@Getter
@Builder
@RedisHash(value = "refresh", timeToLive = 604800)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken implements Serializable {

    @Id
    private Long memberId;

    @Indexed
    private String refresh;

    private Long ttl;
}
