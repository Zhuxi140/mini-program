package com.zhuxi.service.Cache;

import com.zhuxi.utils.RedisUntil;
import com.zhuxi.utils.properties.RedisCacheProperties;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import src.main.java.com.zhuxi.pojo.DTO.Car.CartRedisDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class CartRedisCache {
    private final RedisUntil redisUntil;
    private final RedisCacheProperties rCP;

    public CartRedisCache(RedisUntil redisUntil, RedisCacheProperties rCP) {
        this.redisUntil = redisUntil;
        this.rCP = rCP;
    }

    public List<Long> getIdBySnowflake(Long specSnowflake){
        String specDetailKey = rCP.getProductCache().getSpecDetailPrefix() + ":" + specSnowflake;
        List<Object> ids = redisUntil.hMultiGet(specDetailKey, List.of("productId", "id"));

        ArrayList<Long> id = new ArrayList<>();
        for (Object o : ids){
            if (o instanceof Long){
                id.add((Long) o);
            }else{
                id.add(Long.valueOf((Integer)o));
            }
        }
        return id;
    }


    public void saveProductId(Long specSnowflake,Long productId){
        String specDetailKey = rCP.getProductCache().getSpecDetailPrefix() + ":" + specSnowflake;
        redisUntil.hPut(specDetailKey,"productId",productId);
    }

    public void saveSpecId(Long specSnowflake,Long SpecId){
        String specDetailKey = rCP.getProductCache().getSpecDetailPrefix() + ":" + specSnowflake;
        redisUntil.hPut(specDetailKey,"id",SpecId);
    }

    public String getCartKey(Long userId,Long cartId){
        return rCP.getCartCache().getCartPrefix() + ":" + userId + ":" + cartId;
    }

    public void syncCartInit(List<CartRedisDTO> list,Long userId){
        List<String> cartKeys = new ArrayList<>();
        for (CartRedisDTO cartRedisDTO : list){
            String cartKey = getCartKey(userId, cartRedisDTO.getCarId());
            cartKeys.add(cartKey);
        }
        Map<Object, Object> data = new HashMap<>();
        redisUntil.executePipeline(p -> {
            HashOperations<String, Object, Object> hash = p.opsForHash();
            for (int i =0; i < cartKeys.size(); i++){
                CartRedisDTO cartRedisDTO = list.get(i);
                data.put("id", cartRedisDTO.getCarId());
                data.put("productSnowflake", cartRedisDTO.getProductSnowflake());
                data.put("specSnowflake", cartRedisDTO.getSpecSnowflake());
                data.put("quantity", cartRedisDTO.getQuantity());
                String key = cartKeys.get(i);
                hash.putAll(key, data);
                p.expire(key,7, TimeUnit.DAYS);
                data.clear();
            }
        });
    }


}
