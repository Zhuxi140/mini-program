package com.zhuxi.utils;


import com.zhuxi.Exception.RedisException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Component
public class RedisUntil {

    private StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String,Object> redisTemplate;

    public RedisUntil(StringRedisTemplate stringRedisTemplate, RedisTemplate<String,Object>  redisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisTemplate = redisTemplate;
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
    public void delete(String key){
        stringRedisTemplate.delete( key);
    }

    // 清空前缀为xx的所有键
    public void deleteByPrefix(String prefix){
        Set<String> keys = stringRedisTemplate.keys(prefix + "*");
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }

    // redis List类型操作

    // redis Hash类型操作
    public List<Object> hMultiGet(String key, List<Object> hashKeys){
       return redisTemplate.opsForHash().multiGet(key,hashKeys);
    }

    // 存
    public void hPutMap(String key,Map<?,?>  map){
        redisTemplate.opsForHash().putAll(key, map);
    }

    public void hPut(String key,String hashKey,Object value){
        redisTemplate.opsForHash().put(key,hashKey,value);
    }

    public Object hGet(String key,String hashKey){
        return redisTemplate.opsForHash().get(key, hashKey);
    }


    // redis Set类型操作

    // redis SortedSet类型操作
    public Set<ZSetOperations.TypedTuple<Object>> ZSetReverseRangeScore(String key, Double lastScored,Integer offset,Integer count){
        return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key,Double.NEGATIVE_INFINITY,lastScored,offset,count);
    }

    public Set<ZSetOperations.TypedTuple<Object>> ZSetRangeScore(String key, Double lastScored,Integer offset, Integer count){
        return redisTemplate.opsForZSet().rangeByScoreWithScores(key,lastScored,Double.POSITIVE_INFINITY,offset,count);
    }


    // Pipeline批量操作
    @SuppressWarnings("unchecked")
    public List<Object> executePipeline(Consumer<RedisOperations<String, Object>> pipeline){
        List<Object> objects;
        try {
             objects = redisTemplate.executePipelined(new SessionCallback<List<Object>>() {
                @Override
                public <K, V> List<Object> execute(RedisOperations<K, V> operations) throws DataAccessException {
                    RedisOperations<String, Object> redisOperations = (RedisOperations<String, Object>) operations;
                    pipeline.accept(redisOperations);
                    return null;
                }
            });
        }catch (DataAccessException  e){
            throw new RedisException(e.getMessage());
        }

        if (objects.stream().anyMatch(obj->obj instanceof Throwable)){
            throw new RedisException("Pipeline execution failed");
        }

        return objects;
    }



}
