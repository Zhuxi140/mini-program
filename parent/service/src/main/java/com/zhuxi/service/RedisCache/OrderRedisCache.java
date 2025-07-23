package com.zhuxi.service.RedisCache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zhuxi.utils.JsonUtils;
import com.zhuxi.utils.RedisUntil;
import com.zhuxi.utils.properties.RedisCacheProperties;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class OrderRedisCache {

    private RedisUntil redisUntil;
    private RedisCacheProperties rCP;

    public OrderRedisCache(RedisUntil redisUntil, RedisCacheProperties rCP) {
        this.redisUntil = redisUntil;
        this.rCP = rCP;
    }


    public String gerOrderLockKey(String orderSn) {
        return rCP.getOrderCache().getOrderLockPrefix() +":" + orderSn;
    }

    public void saveOrderLock(String orderSn,Integer productQuantity) {
        String key = gerOrderLockKey(orderSn);
        redisUntil.hPut(key,"Quantity",productQuantity);
    }

    public Integer getOrderLock(String orderSn) {
        String key = gerOrderLockKey(orderSn);
        Object o = redisUntil.hGet(key, "Quantity");
        return o == null ? null : (Integer) o;
    }

    public void deleteLockKey(String key){
        String keys = gerOrderLockKey(key);
        redisUntil.delete(keys);
    }



}
