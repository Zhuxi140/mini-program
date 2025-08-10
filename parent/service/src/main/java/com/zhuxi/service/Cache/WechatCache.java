package com.zhuxi.service.Cache;

import com.zhuxi.utils.RedisUntil;
import com.zhuxi.utils.properties.RedisCacheProperties;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class WechatCache {


    private final RedisCacheProperties rCP;
    private final RedisUntil redisUntil;


    public WechatCache(RedisCacheProperties rCP, RedisUntil redisUntil) {
        this.rCP = rCP;
        this.redisUntil = redisUntil;
    }

    public String getAccessionTokenKey(){
        return rCP.getWechatCache().getAccessTokenPrefix();
    }

    public void saveAccessionToken(String value, long time, TimeUnit unit){
        redisUntil.setStringValue(getAccessionTokenKey(),value,time,unit);
    }

    public String getAccessionToken(){
        return (String) redisUntil.getStringValue(getAccessionTokenKey());
    }


}
