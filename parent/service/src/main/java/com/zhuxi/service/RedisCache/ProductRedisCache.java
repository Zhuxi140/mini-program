package com.zhuxi.service.RedisCache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zhuxi.Constant.Message;
import com.zhuxi.Exception.RedisException;
import com.zhuxi.utils.JsonUtils;
import com.zhuxi.utils.RedisUntil;
import com.zhuxi.utils.properties.RedisCacheProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductOverviewVO;
import java.math.BigDecimal;
import java.sql.Array;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ProductRedisCache {

    private final RedisUntil redisUntil;
    private final RedisCacheProperties rCP;

    public ProductRedisCache(RedisUntil redisUntil, RedisCacheProperties rCP) {
        this.redisUntil = redisUntil;
        this.rCP = rCP;
    }

    public String getSortPriceKey(){
        return rCP.getProductCache().getZSetPrefix() + ":price:asc";
    }

    public String getSortCreateDesc(){
        return rCP.getProductCache().getZSetPrefix() + ":create:" + "desc";
    }

    public String getProductDetailKey(String productId){
        return rCP.getProductCache().getHashPrefix() + ":" + productId;
    }

    public Set<ZSetOperations.TypedTuple<Object>> getListProductsIds(String key, Integer type, Long lastScore, Integer pageSize){

            Set<ZSetOperations.TypedTuple<Object>> IdResults = null;
            switch ( type){
                case 1, 3:{
                    IdResults = redisUntil.ZSetReverseRangeScore(
                            key,
                            lastScore ==  0L ? Double.POSITIVE_INFINITY : lastScore,
//                            lastScore == 0D ?  0 : 1,
                            0,
                            pageSize);
                }
                    break;
                case 2:{
                    IdResults = redisUntil.ZSetRangeScore(
                            key,
                            lastScore == 0L ? Double.NEGATIVE_INFINITY : lastScore,
//                            lastScore == 0D ?  0 : 1,
                            0,
                            pageSize);
                }
                    break;
            }
            if (CollectionUtils.isEmpty(IdResults)){
                log.error("获取的商品缓存id为null");
                return null;
            }
        return IdResults;
    }

    public Long getLastScore(Set<ZSetOperations.TypedTuple<Object>>  Score){
        if(CollectionUtils.isEmpty( Score)){
            return null;
        }
        ArrayList<ZSetOperations.TypedTuple<Object>> list = new ArrayList<>(Score);
        return Objects.requireNonNull(list.get(list.size() - 1).getScore()).longValue();
    }

    public List<ProductOverviewVO> getListProductsCache(Set<ZSetOperations.TypedTuple<Object>>  Score,Integer pageSize){
        Set<String> productIds = Score.stream().limit(pageSize).map(t -> Objects.requireNonNull(t.getValue()).toString()).collect(Collectors.toCollection(LinkedHashSet::new));

        List<String> collect = productIds.stream()
                .map(this::getProductDetailKey)
                .toList();
        Collection<Object> fieldCollection = new ArrayList<>(Arrays.asList("id", "name", "price", "coverUrl","createAt"));

        List<Object> results = redisUntil.executePipeline(pipe -> {
            collect.forEach(key -> pipe.opsForHash().multiGet(key, fieldCollection));
        });

        if (CollectionUtils.isEmpty(results)){
            return null;
        }

        return results.stream().map(map -> {
            if (!(map instanceof List< ?> list)){
                throw new RedisException(Message.TYPE_TURN_ERROR);
            }
            ProductOverviewVO pOVO = new ProductOverviewVO();
            pOVO.setId(Long.parseLong((String) list.get(0)));
            pOVO.setName((String) list.get(1));
            pOVO.setPrice(new BigDecimal((String) list.get(2)));
            pOVO.setCoverUrl((String) list.get(3));
            LocalDateTime localDateTime = Instant.ofEpochMilli((Long) list.get(4)).atZone(ZoneId.systemDefault()).toLocalDateTime();
            pOVO.setCreatedAt(localDateTime);
            return pOVO;
        }).toList();

    }

    public void syncProduct(List<ProductOverviewVO> product){
        redisUntil.executePipeline(pipe-> {
            product.forEach(p->{
                long epochMilli = p.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                Long id = p.getId();
                String productId = id.toString();
                BigDecimal price = p.getPrice();
                Long priceInt = price.multiply(BigDecimal.valueOf(100)).longValueExact();
                Long priceId = generatePriceId(priceInt, id);
                String priceStr = price.toPlainString();
                String productDetailKey = getProductDetailKey(productId);
                String sortPriceASC = getSortPriceKey();
                String sortCreateDesc = getSortCreateDesc();


                pipe.opsForHash().putAll(productDetailKey,Map.of(
                        "id",productId,
                        "name",p.getName(),
                        "coverUrl",p.getCoverUrl(),
                        "price",priceStr,
                        "createAt",epochMilli
                ));


                pipe.opsForZSet().add(sortCreateDesc,productId,epochMilli);
                pipe.opsForZSet().add(sortPriceASC,productId,priceId);

                pipe.expire(productDetailKey,rCP.getProductCache().getDetailTTL(), TimeUnit.DAYS);
                pipe.expire(sortCreateDesc,rCP.getProductCache().getCreateTTL(), TimeUnit.DAYS);
                pipe.expire(sortPriceASC,rCP.getProductCache().getPriceTTL(), TimeUnit.HOURS);
            });
        });
    }

    public void syncProductInit(List<ProductDetailVO> product){
        redisUntil.executePipeline(pipe-> {
            product.forEach(p->{
                long epochMilli = p.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                Long id = p.getId();
                String productId = id.toString();
                BigDecimal price = p.getPrice();
                Long priceInt = price.multiply(BigDecimal.valueOf(100)).longValueExact();
                Long priceId = generatePriceId(priceInt, id);
                String priceStr = price.toPlainString();
                String productDetailKey = getProductDetailKey(productId);
                String sortPriceASC = getSortPriceKey();
                String sortCreateDesc = getSortCreateDesc();

                HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
                objectObjectHashMap.put("id",productId);
                objectObjectHashMap.put("name",p.getName());
                objectObjectHashMap.put("coverUrl",p.getCoverUrl());
                objectObjectHashMap.put("price",priceStr);
                objectObjectHashMap.put("createAt",epochMilli);
                objectObjectHashMap.put("description",p.getDescription());
                objectObjectHashMap.put("status",p.getStatus());
                objectObjectHashMap.put("origin",p.getOrigin());
                objectObjectHashMap.put("images",p.getImages());
                pipe.opsForHash().putAll(productDetailKey,objectObjectHashMap);

                pipe.opsForZSet().add(sortCreateDesc,productId,epochMilli);
                pipe.opsForZSet().add(sortPriceASC,productId,priceId);

                pipe.expire(productDetailKey,rCP.getProductCache().getDetailTTL(), TimeUnit.DAYS);
                pipe.expire(sortCreateDesc,rCP.getProductCache().getCreateTTL(), TimeUnit.DAYS);
                pipe.expire(sortPriceASC,rCP.getProductCache().getPriceTTL(), TimeUnit.HOURS);
            });
        });
    }

    public ProductDetailVO getCacheDetails(Long id){
        String productId = id.toString();
        String productDetailKey = getProductDetailKey(productId);
        ArrayList<Object> objects = new ArrayList<>(Arrays.asList("name","price","coverUrl","description","status","origin","images"));
        List<Object> objects1 = redisUntil.hMultiGet(productDetailKey, objects);

        if (objects1.get(0) == null){
            return null;
        }
        //解析结果

        ProductDetailVO pDVO = new ProductDetailVO();
        pDVO.setName((String) objects1.get(0));
        pDVO.setPrice(new BigDecimal((String) objects1.get(1)));
        pDVO.setCoverUrl((String) objects1.get(2));
        pDVO.setDescription((String) objects1.get(3));
        pDVO.setStatus((Integer) objects1.get(4));
        pDVO.setOrigin((String) objects1.get(5));
        Object o = objects1.get(6);
        List<String> list = JsonUtils.objectToObject(o, new TypeReference<>() {
        });
        pDVO.setImages(list);

        return pDVO;
    }


    public Long generatePriceId(Long priceInt, Long id){
        if (priceInt > (1L << 23)){
            throw new IllegalArgumentException("价格超过34位上限");
        }
        if (id > (1L << 30)){
            throw new IllegalArgumentException("id超过30位上限");
        }

        return (priceInt << 30) | id;
    }
}
