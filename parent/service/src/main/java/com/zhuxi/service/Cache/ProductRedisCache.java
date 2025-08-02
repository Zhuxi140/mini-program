package com.zhuxi.service.Cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Exception.RedisException;
import com.zhuxi.utils.JacksonUtils;
import com.zhuxi.utils.RedisUntil;
import com.zhuxi.utils.properties.RedisCacheProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import src.main.java.com.zhuxi.pojo.DTO.product.PIdSnowFlake;
import src.main.java.com.zhuxi.pojo.DTO.product.SpecRedisDTO;
import src.main.java.com.zhuxi.pojo.DTO.product.snowFlakeMap;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductOverviewVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductSpecVO;

import java.math.BigDecimal;
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

    public String getSpecDetailKey(Long snowflakeId){
        return rCP.getProductCache().getSpecDetailPrefix() + ":" + snowflakeId;
    }

    public String getMapSpecProductKey(){
        return rCP.getProductCache().getSpecMapProductPrefix();
    }

    public String getStockKey(Long specId){
        return rCP.getProductCache().getStockPrefix() + ":" + specId;
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

        Iterator<String> iterator = productIds.iterator();

        return results.stream().map(map -> {
            if (!(map instanceof List< ?> list)){
                throw new RedisException(MessageReturn.TYPE_TURN_ERROR);
            }
            String productNumber = iterator.next();
            ProductOverviewVO pOVO = new ProductOverviewVO();
            pOVO.setProductNumber(Long.valueOf(productNumber));
            pOVO.setId(Long.valueOf((Integer)list.get(0)));
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

    public void SyncProductMQ(Map<String,Object>  product,Long snowflakeId){
        String productDetailKey = getProductDetailKey(snowflakeId.toString());
        redisUntil.hPutMap(productDetailKey,product);
    }

    public void SyncSpecMQ(Map<String,Object> spec,Long snowflakeId,Integer stock){
        String specKey = getSpecDetailKey(snowflakeId);
        redisUntil.hPutMap(specKey,spec);
        redisUntil.setStringValue(getStockKey(snowflakeId), String.valueOf(stock),10,TimeUnit.MINUTES);
    }

    public void syncProductInit(List<ProductDetailVO> product){
        String sortPriceASC = getSortPriceKey();
        String sortCreateDesc = getSortCreateDesc();
        HashMap<Object, Object> HashMap = new HashMap<>();

        redisUntil.executePipeline(pipe-> {
            product.forEach(p->{
                long epochMilli = p.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                Long id = p.getId();
                String SnowflakeId = p.getSnowflakeId().toString();
                BigDecimal price = p.getPrice();
                Long priceInt = price.multiply(BigDecimal.valueOf(100)).longValueExact();
                Long priceId = generatePriceId(priceInt, id);
                String priceStr = price.toPlainString();
                String productDetailKey = getProductDetailKey(SnowflakeId);
                HashMap.put("id",id);
                HashMap.put("name",p.getName());
                HashMap.put("coverUrl",p.getCoverUrl());
                HashMap.put("price",priceStr);
                HashMap.put("createAt",epochMilli);
                HashMap.put("description",p.getDescription());
                HashMap.put("status",p.getStatus());
                HashMap.put("origin",p.getOrigin());
                HashMap.put("images",p.getImages());
                pipe.opsForHash().putAll(productDetailKey,HashMap);

                pipe.opsForZSet().add(sortCreateDesc,SnowflakeId,epochMilli);
                pipe.opsForZSet().add(sortPriceASC,SnowflakeId,priceId);
                pipe.expire(productDetailKey,rCP.getProductCache().getDetailTTL(), TimeUnit.DAYS);

                HashMap.clear();
            });

            pipe.expire(sortCreateDesc,rCP.getProductCache().getCreateTTL(), TimeUnit.DAYS);
            pipe.expire(sortPriceASC,rCP.getProductCache().getPriceTTL(), TimeUnit.HOURS);
        });
    }

    public void syncProductOne(ProductDetailVO product){
        String sortPriceASC = getSortPriceKey();
        String sortCreateDesc = getSortCreateDesc();
        HashMap<Object, Object> HashMap = new HashMap<>();
        redisUntil.executePipeline(pipe-> {
                long epochMilli = product.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                Long id = product.getId();
                String SnowflakeId = product.getSnowflakeId().toString();
                BigDecimal price = product.getPrice();
                Long priceInt = price.multiply(BigDecimal.valueOf(100)).longValueExact();
                Long priceId = generatePriceId(priceInt, id);
                String priceStr = price.toPlainString();
                String productDetailKey = getProductDetailKey(SnowflakeId);
                HashMap.put("id",id);
                HashMap.put("name",product.getName());
                HashMap.put("coverUrl",product.getCoverUrl());
                HashMap.put("price",priceStr);
                HashMap.put("createAt",epochMilli);
                HashMap.put("description",product.getDescription());
                HashMap.put("status",product.getStatus());
                HashMap.put("origin",product.getOrigin());
                HashMap.put("images",product.getImages());
                pipe.opsForHash().putAll(productDetailKey,HashMap);
                pipe.opsForZSet().add(sortCreateDesc,SnowflakeId,epochMilli);
                pipe.opsForZSet().add(sortPriceASC,SnowflakeId,priceId);
                pipe.expire(productDetailKey,rCP.getProductCache().getDetailTTL(), TimeUnit.DAYS);
                pipe.expire(sortCreateDesc,rCP.getProductCache().getCreateTTL(), TimeUnit.DAYS);
                pipe.expire(sortPriceASC,rCP.getProductCache().getPriceTTL(), TimeUnit.HOURS);
        });
    }

    public void syncSpecInit(List<SpecRedisDTO> spec,List<PIdSnowFlake> saleProductId){
        HashMap<Object, Object> HashMap = new HashMap<>();
        String mapSpecProductKey = getMapSpecProductKey();

        Map<String, List<Long>> productMapSpec = sortMap(spec, saleProductId);

        redisUntil.executePipeline(p->{
            HashOperations<String, Object, Object> hash = p.opsForHash();
            ValueOperations<String, Object> value = p.opsForValue();

                hash.putAll(mapSpecProductKey,productMapSpec);
            p.expire(mapSpecProductKey,12,TimeUnit.HOURS);

            spec.forEach(s->{
                Long snowflakeId = s.getSnowflakeId();
                String specDetailKey = getSpecDetailKey(snowflakeId);
                String stockKey = getStockKey(snowflakeId);
                Integer stock = s.getStock();
                HashMap.put("id",s.getId());
                HashMap.put("productId",s.getProductId());
                HashMap.put("spec",s.getSpec());
                HashMap.put("salePrice",s.getSalePrice());
                HashMap.put("coverUrl",s.getCoverUrl());
                hash.putAll(specDetailKey,HashMap);
                value.set(stockKey,stock);

                HashMap.clear();
                p.expire(stockKey,10,TimeUnit.MINUTES);
            });
        });
    }

    public ProductDetailVO getCacheDetails(Long ProductNumber){
        String ProductNumberS = ProductNumber.toString();
        String productDetailKey = getProductDetailKey(ProductNumberS);
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
        List<String> list = JacksonUtils.objectToObject(o, new TypeReference<>() {
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

    public List<ProductSpecVO> getProductSpec(Long productNumber){
        String mapSpecProductKey = getMapSpecProductKey();
        Object result = redisUntil.hGet(mapSpecProductKey, productNumber.toString());
        List<Long> specNumbers = JacksonUtils.objectToObject(result, new TypeReference<>() {
        });
        Collection<Object> filed = new ArrayList<>(Arrays.asList("spec", "salePrice","purchasePrice","coverUrl"));

        List<Object> objects = redisUntil.executePipeline(pipe -> {
            for (int i = 0; i < specNumbers.size(); i++){
                String specDetailKey = getSpecDetailKey(specNumbers.get(i));
                pipe.opsForHash().multiGet(specDetailKey, filed);
            }
        });
        ArrayList<ProductSpecVO> vos = new ArrayList<>();

        for (int i =  0; i < objects.size(); i++) {
            if (!(objects.get(i) instanceof List<?> lists)){
                throw new IllegalArgumentException("数据类型错误");
            }
            ProductSpecVO vo = new ProductSpecVO();
            vo.setSpec((String) lists.get(0));
            vo.setPrice(BigDecimal.valueOf((Double) lists.get(1)));
            vo.setCoverUrl((String) lists.get(3));
            vo.setSpecNumber(specNumbers.get(i));
            vos.add(vo);
        }

        if (vos.isEmpty()){
            return null;
        }

        return vos;
    }

    public Long getProductIdBySnowflakeId(Long snowflake_id) {

        String productDetailKey = getProductDetailKey(snowflake_id.toString());
        return (Long) redisUntil.hGet(productDetailKey, "id");
    }

    public void deleteProduct(Long productId,List<Long> specNumbers) {
        String product = productId.toString();
        redisUntil.delete(getProductDetailKey(product));
        redisUntil.deleteZSetOneFiled(getSortPriceKey(),productId);
        redisUntil.deleteZSetOneFiled(getSortCreateDesc(),productId);
        redisUntil.hDelete(getMapSpecProductKey(),product);
        for(int i =0; i < specNumbers.size(); i++){
            Long specSnowFlake = specNumbers.get(i);
            redisUntil.delete(getSpecDetailKey(specSnowFlake));
            redisUntil.delete(getStockKey(specSnowFlake));
        }
    }

    public void syncSPMap(List<snowFlakeMap> snowFlakeMaps){
        Map<Long, Object> objectObjectHashMap = new HashMap<>();
    }

    public Map<String, List<Long>> sortMap(List<SpecRedisDTO> spec,List<PIdSnowFlake> saleProductId) {
        Map<String,List<Long>> productMapSpec = new HashMap<>();
        Map<Long, String> productIdToSnowflakeMap = new HashMap<>();
        saleProductId.forEach(s -> {
            productIdToSnowflakeMap.put(s.getId(), s.getSnowflakeId().toString());
        });

        spec.forEach(specs -> {
            Long productId = specs.getProductId();
            String productSnowflake = productIdToSnowflakeMap.get(productId);

            if (productSnowflake != null) {
                productMapSpec.computeIfAbsent(productSnowflake, k -> new ArrayList<>())
                        .add(specs.getSnowflakeId());
            }
        });

        return productMapSpec;
    }

}
