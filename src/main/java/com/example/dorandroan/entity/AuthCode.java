package com.example.dorandroan.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Entity
@Builder
@RedisHash(value = "code", timeToLive = 3600)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthCode implements Serializable {

    @Id
    private String email;

    private Integer code;
}
