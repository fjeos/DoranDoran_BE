package com.example.dorandroan.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@Entity
@Builder
@RedisHash(value = "refresh", timeToLive = 604800)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken implements Serializable {

    @Id
    private Long userId;

    @Indexed
    private String refresh;

    private Long ttl;
}
