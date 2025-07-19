package com.zhuxi.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String, Object> RedisTemplate = new RedisTemplate<>();
        RedisTemplate.setConnectionFactory( factory);
        GenericJackson2JsonRedisSerializer json = new GenericJackson2JsonRedisSerializer();
        RedisTemplate.setKeySerializer(RedisSerializer.string());
        RedisTemplate.setHashKeySerializer(RedisSerializer.string());

        RedisTemplate.setValueSerializer(json);
        RedisTemplate.setHashValueSerializer(json);

        RedisTemplate.afterPropertiesSet();
        return RedisTemplate;
    }


}
