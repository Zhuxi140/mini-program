package com.zhuxi.service.Cache;

import com.zhuxi.utils.RedisUntil;
import com.zhuxi.utils.properties.RedisCacheProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PayRedisCache {

    private final RedisCacheProperties rCP;
    private final RedisUntil redisUntil;

    public PayRedisCache(RedisCacheProperties rCP, RedisUntil redisUntil) {
        this.rCP = rCP;
        this.redisUntil = redisUntil;
    }

    public String getOrderStatusKey(Integer status){
        return rCP.getOrderCache().getOrderStatusPrefix() + ":" + status;
    }

    public String getOrderDetailKey(String orderSn){
        return rCP.getOrderCache().getOrderDetailHashPrefix() +":"+ orderSn;
    }

    public void refreshOrder(String orderSn){
        String orderDetailKey = getOrderDetailKey(orderSn);
        Long l1 = redisUntil.deleteZSetOneFiled(getOrderStatusKey(0), orderSn);
        redisUntil.hPut(orderDetailKey,"status",1);
        redisUntil.expire(orderDetailKey,7, TimeUnit.DAYS);
    }
}
