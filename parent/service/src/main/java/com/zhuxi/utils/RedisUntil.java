package com.zhuxi.utils;


import com.zhuxi.Exception.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.sql.DriverManager.getConnection;

@Component
@Slf4j
public class RedisUntil {

    private final ValueOperations<String, Object> stringValueOperations;
    private final RedisTemplate<String,Object> redisTemplate;
    private final ZSetOperations<String, Object> zSetOperations;
    private final HashOperations<String, Object, Object> HashOperations;

    public RedisUntil(RedisTemplate<String,Object>  redisTemplate) {
        stringValueOperations = redisTemplate.opsForValue();
        this.redisTemplate = redisTemplate;
        zSetOperations = redisTemplate.opsForZSet();
        HashOperations = redisTemplate.opsForHash();
    }


    public boolean hsaKey(String key){
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }



    //redis String类型操作
    public Object getStringValue(String key){
        return stringValueOperations.get(key);
    }


    public void setStringValue(String key,String value,long time,TimeUnit unit){
        stringValueOperations.set(key,value,time, unit);
    }

    //清空对应key
    public Boolean delete(String key){
         return redisTemplate.delete(key);
    }




    // redis List类型操作

    // redis Hash类型操作
    public List<Object> hMultiGet(String key, Collection<Object> hashKeys){
       return HashOperations.multiGet(key,hashKeys);
    }

    public void hPutMap(String key,Map<?,?>  map){
        HashOperations.putAll(key, map);
    }

    public void hPut(String key,String hashKey,Object value){
        HashOperations.put(key,hashKey,value);
    }

    public Object hGet(String key,String hashKey){
        return HashOperations.get(key, hashKey);
    }

    public Long hDelete(String key,Object... hashKey){
        return HashOperations.delete(key, hashKey);
    }

    // 设置过期时间
    public void expire(String key, long time, TimeUnit unit){
        redisTemplate.expire(key, time, unit);
    }


    // redis Set类型操作

    // redis SortedSet类型操作
    public Set<ZSetOperations.TypedTuple<Object>> ZSetReverseRangeScore(String key, Double lastScored,Integer offset,Integer count){
        return zSetOperations.reverseRangeByScoreWithScores(key,Double.NEGATIVE_INFINITY,lastScored,offset,count);
    }


    public Set<ZSetOperations.TypedTuple<Object>> ZSetRangeScore(String key, Double lastScored,Integer offset, Integer count){
        return zSetOperations.rangeByScoreWithScores(key,lastScored,Double.POSITIVE_INFINITY,offset,count);
    }

    public Long deleteZSetOneFiled(String key,Object value){
        return zSetOperations.remove(key, value);
    }
   public Set<Object> ZSetRangeFirstElement(String key){
       return zSetOperations.range(key, 0, 0);
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


    /**
     * 批量删除键 支持通配符（仅使用小数据量）
     */
    public void deleteAllCart(String  key) {
        ScanOptions options = ScanOptions.scanOptions()
                .match(key)
                .count(100)
                .build();

        Long totalDeleted = 0L;
        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            List<String> batchKeys = new ArrayList<>();
            while (cursor.hasNext()) {
                batchKeys.add(cursor.next());
            }
            totalDeleted += redisTemplate.delete(batchKeys);
            log.info("Deleted keys:{}  sumCount:{}", key,totalDeleted); // 精简日志
        } catch (Exception e) {
            log.error("Failed to delete cart", e);
        }
    }

    /**
     * 批量获取多个Hash结构的数据（修复序列化问题）
     * @param keys 要查询的键列表
     * @param fields 要查询的字段列表
     * @return Map<key, Map<field, value>>
     */
    public Map<String, Map<Object, Object>> hMultiGetBatch(
            List<String> keys,
            List<String> fields) {

        if (keys == null || keys.isEmpty() || fields == null || fields.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Map<Object, Object>> results = new LinkedHashMap<>();
        RedisSerializer<String> stringSerializer = RedisSerializer.string();

        // 转换为正确的字段序列化格式
        List<byte[]> serializedFields = fields.stream()
                .map(stringSerializer::serialize)
                .toList();

        // 使用原生executePipelined避免序列化问题
        List<Object> pipelineResults = redisTemplate.executePipelined(
                (RedisCallback<Object>) connection -> {
                    for (String key : keys) {
                        byte[] keyBytes = stringSerializer.serialize(key);
                        byte[][] fieldBytesArray = serializedFields.toArray(new byte[0][]);
                        connection.hMGet(keyBytes, fieldBytesArray);
                    }
                    return null;
                },
                stringSerializer
        );

        // 处理结果
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object result = pipelineResults.get(i);

            if (result instanceof List) {
                List<Object> values = (List<Object>) result;
                Map<Object, Object> fieldMap = new HashMap<>();

                for (int j = 0; j < fields.size(); j++) {
                    if (j < values.size()) {
                        fieldMap.put(fields.get(j), values.get(j));
                    } else {
                        fieldMap.put(fields.get(j), null);
                    }
                }
                results.put(key, fieldMap);
            } else {
                results.put(key, Collections.emptyMap());
                log.warn("Unexpected result type for key {}: {}", key,
                        result != null ? result.getClass() : "null");
            }
        }

        return results;
    }






}
