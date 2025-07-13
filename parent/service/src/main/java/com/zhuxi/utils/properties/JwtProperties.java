package com.zhuxi.utils.properties;


import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;

@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private final String secretKey;
    private final long expirationTime;

    public JwtProperties(String secretKey, long expirationTime) {
        this.secretKey = secretKey;
        this.expirationTime = expirationTime;
    }

    public SecretKey getSecretKey() {
        // 获取密钥
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public long getExpirationTime() {
        return expirationTime;
    }


}
