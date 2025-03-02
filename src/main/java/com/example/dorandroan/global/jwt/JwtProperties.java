package com.example.dorandroan.global.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
@Getter
public class JwtProperties {
    private Access access;
    private Refresh refresh;

    public JwtProperties(Access access, Refresh refresh) {
        this.access = access;
        this.refresh = refresh;
    }

    @Getter
    @AllArgsConstructor
    public static class Access {
        private String secret;
        private long expiration;
    }

    @Getter
    @AllArgsConstructor
    public static class Refresh {
        private String secret;
        private long expiration;
    }
}
