package com.zhuxi.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String, Object> RedisTemplate = new RedisTemplate<>();
        RedisTemplate.setConnectionFactory( factory);
//        GenericJackson2JsonRedisSerializer json = new GenericJackson2JsonRedisSerializer();
        RedisTemplate.setKeySerializer(RedisSerializer.string());
        RedisTemplate.setHashKeySerializer(RedisSerializer.string());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(objectMapper,Object.class);
        RedisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        RedisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        RedisTemplate.afterPropertiesSet();
        return RedisTemplate;
    }


}
