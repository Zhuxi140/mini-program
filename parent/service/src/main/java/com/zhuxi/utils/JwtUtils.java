package com.zhuxi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhuxi.utils.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Map;



@Component
public class JwtUtils {

    private final JwtProperties jwtProperties;

    public JwtUtils(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }


    /**
     * 创建token
     * @param claims  存储数据
     * @return  token
     */
    public String createToken(Map<String,Object> claims){
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationTime() * 1000))  //
                .signWith(jwtProperties.getSecretKey())
                .compact();
    }

    /**
     * 解析token
     * @param token  token
     * @return  claims
     */
    public Claims parseToken(String token){
        int dotCount = 0;
        for(int i=0; i< token.length();i++){
            if(token.charAt(i) == '.')
                dotCount++;
        }

        if(dotCount != 2)
            throw new MalformedJwtException("token格式有误");

        String headerBase64 = token.split("\\.")[0];
        String headerJson = new String(Base64.getDecoder().decode(headerBase64), StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.readTree(headerJson);
        } catch (JsonProcessingException e) {
            throw new MalformedJwtException(e.getMessage());
        }

        return Jwts.parser()
                .verifyWith(jwtProperties.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
