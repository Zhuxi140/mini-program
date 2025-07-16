package com.zhuxi.utils;


import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUntil {

    private StringRedisTemplate stringRedisTemplate;

    public RedisUntil(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }



    //redis String类型操作
    public String getStringValue(String key){
        return stringRedisTemplate.opsForValue().get(key);
    }

    //并设置时间
    public void setStringValue(String key,String value,long time){
        stringRedisTemplate.opsForValue().set(key,value,time, TimeUnit.MINUTES);
    }

    //清空对应key
    public void deleteStringValue(String key){
        stringRedisTemplate.delete(key);
    }

    // redis List类型操作

    // redis Hash类型操作

    // redis Set类型操作

    // redis SortedSet类型操作


}
