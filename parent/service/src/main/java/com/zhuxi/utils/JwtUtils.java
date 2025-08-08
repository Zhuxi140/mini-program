package com.zhuxi.utils;

import com.zhuxi.utils.properties.JwtProperties;
import io.jsonwebtoken.*;
import com.zhuxi.Exception.JwtException;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Map;
import java.util.UUID;


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
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationTime() * 1000))  //
                .signWith(jwtProperties.getSecretKey())
                .compact();
    }

    /**
     * 解析token
     */
    public Claims parseToken(String token){
            int dotCount = 0;
            for (int i = 0; i < token.length(); i++) {
                if (token.charAt(i) == '.')
                    dotCount++;
            }

            if (dotCount != 2)
                throw new MalformedJwtException("token格式有误");
            token = token.replaceAll("\\s", "");
    try {
            return jwtParser
                    .parseSignedClaims(token)
                    .getPayload();
    } catch (MalformedJwtException e) {
        throw new JwtException("Token格式有误--:" + e.getMessage());
    }catch (ExpiredJwtException e){
        throw new JwtException("Token过期--:" + e.getMessage());
    }catch (Exception e){
        throw new JwtException(e.getMessage());
    }
    }


    /**
     * 验证token合法性
     */
    public boolean verifyToken(String token){
            parseToken(token);
            return true;
    }




    /**
     * 计算字符在字符串中出现的次数
     */
    private int countOccurrences(String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }



}
