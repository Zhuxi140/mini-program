package com.zhuxi.service.Cache;


import com.zhuxi.utils.RedisUntil;
import com.zhuxi.utils.properties.RedisCacheProperties;
import org.springframework.stereotype.Service;

@Service
public class AddressRedisCache {

    private final RedisUntil redisUntil;
    private final RedisCacheProperties redisCacheProperties;

    public AddressRedisCache(RedisUntil redisUntil, RedisCacheProperties redisCacheProperties) {
        this.redisUntil = redisUntil;
        this.redisCacheProperties = redisCacheProperties;
    }


}
