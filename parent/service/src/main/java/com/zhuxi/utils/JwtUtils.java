package com.zhuxi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhuxi.utils.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import com.zhuxi.Exception.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Map;



@Slf4j
@Component
public class JwtUtils {

    private final JwtProperties jwtProperties;
    private final JwtParser jwtParser;

    public JwtUtils(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        jwtParser = Jwts.parser()
                .json(new JacksonDeserializer<>())
                .verifyWith(jwtProperties.getSecretKey())
                .build();
    }


    /**
     * 创建token
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
     */
    public Claims parseToken(String token){
        try {
            int dotCount = 0;
            for (int i = 0; i < token.length(); i++) {
                if (token.charAt(i) == '.')
                    dotCount++;
            }

            if (dotCount != 2)
                throw new MalformedJwtException("token格式有误");

            String headerBase64 = token.split("\\.")[0];
            String headerJson = new String(Base64.getDecoder().decode(headerBase64), StandardCharsets.UTF_8);

            ObjectMapper objectMapper = new ObjectMapper();

                objectMapper.readTree(headerJson);

            return jwtParser
                    .parseSignedClaims(token)
                    .getPayload();
    } catch (io.jsonwebtoken.JwtException | JsonProcessingException e) {
        throw new JwtException(e.getMessage());
    }
    }


    /**
     * 验证token合法性
     */
    public boolean verifyToken(String token){
        try {
            parseToken(token);
            return true;
        }catch (JwtException e){
            return false;
        }
    }




}
