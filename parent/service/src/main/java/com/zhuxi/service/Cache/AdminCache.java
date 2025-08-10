package com.zhuxi.service.Cache;

import com.zhuxi.utils.RedisUntil;
import com.zhuxi.utils.properties.RedisCacheProperties;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AdminCache {

    private final RedisUntil redisUntil;
    private final RedisCacheProperties rCP;

    public AdminCache(RedisUntil redisUntil, RedisCacheProperties rCP) {
        this.redisUntil = redisUntil;
        this.rCP = rCP;
    }

    private String getLogOutKey(String token)
    {
        return rCP.getAdminCache().getAdminTokenPrefix() + ":" + token;
    }

    public void saveLogOutToken(String token, String value, long time, TimeUnit unit){
        String logOutKey = getLogOutKey(token);
        redisUntil.setStringValue(logOutKey,value,time,unit);
    }

    public String getLogOutValue(String token){
        String logOutKey = getLogOutKey(token);
        return (String) redisUntil.getStringValue(logOutKey);
    }


}
