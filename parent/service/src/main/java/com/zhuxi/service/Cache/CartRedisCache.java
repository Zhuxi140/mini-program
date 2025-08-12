package com.zhuxi.service.Cache;

import com.zhuxi.pojo.DTO.Cart.CartAddDTO;
import com.zhuxi.pojo.DTO.Cart.CartUpdateDTO;
import com.zhuxi.pojo.VO.Car.CartNewVO;
import com.zhuxi.utils.RedisUntil;
import com.zhuxi.utils.properties.RedisCacheProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.zhuxi.pojo.DTO.Cart.CartRedisDTO;
import com.zhuxi.pojo.VO.Car.CartVO;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
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

    public String getCartKey(Long userId,Object cartId){
        return rCP.getCartCache().getCartPrefix() + ":" + userId + ":" + cartId;
    }

    public String getSortKey(Long userId){
        return rCP.getCartCache().getsSetPrefix() + ":" + userId;
    }

    public void syncCartInit(List<CartRedisDTO> list,Long userId){
        String sortKey = getSortKey(userId);
        List<String> cartKeys = new ArrayList<>();
        for (CartRedisDTO cartRedisDTO : list){
            String cartKey = getCartKey(userId, cartRedisDTO.getCarId());
            cartKeys.add(cartKey);
        }
        Map<Object, Object> data = new HashMap<>();
        redisUntil.executePipeline(p -> {
            HashOperations<String, Object, Object> hash = p.opsForHash();
            ZSetOperations<String, Object> ZSet = p.opsForZSet();
            for (int i =0; i < cartKeys.size(); i++){
                CartRedisDTO cartRedisDTO = list.get(i);
                Long carId = cartRedisDTO.getCarId();
                data.put("id",carId );
                data.put("productSnowflake", cartRedisDTO.getProductSnowflake());
                data.put("specSnowflake", cartRedisDTO.getSpecSnowflake());
                data.put("quantity", cartRedisDTO.getQuantity());
                String key = cartKeys.get(i);
                hash.putAll(key, data);
                ZSet.add(sortKey,carId,carId);
                p.expire(key,7, TimeUnit.DAYS);
                data.clear();
            }
            p.expire(sortKey,7, TimeUnit.DAYS);
        });
    }

    public void syncCartLack(List<CartVO> list){
        Long userId = list.get(0).getUserId();
        String sortKey = getSortKey(userId);
        List<String> cartKeys = new ArrayList<>();
        for (CartVO cartVO : list){
            String cartKey = getCartKey(userId, cartVO.getId());
            cartKeys.add(cartKey);
        }
        Map<Object, Object> data = new HashMap<>();
        redisUntil.executePipeline(p -> {
            HashOperations<String, Object, Object> hash = p.opsForHash();
            ZSetOperations<String, Object> ZSet = p.opsForZSet();
            for (int i =0; i < cartKeys.size(); i++){
                CartVO cartVO = list.get(i);
                Long carId = cartVO.getId();
                data.put("id",carId );
                data.put("productSnowflake", cartVO.getProductSnowflake());
                data.put("specSnowflake", cartVO.getSpecSnowflake());
                data.put("quantity", cartVO.getQuantity());
                String key = cartKeys.get(i);
                hash.putAll(key, data);
                ZSet.add(sortKey,carId,carId);
                p.expire(key,7, TimeUnit.DAYS);
                data.clear();
            }
            p.expire(sortKey,7, TimeUnit.DAYS);
        });
    }


    public Boolean deleteCart(Long userId,Long cartId){
        String cartKey = getCartKey(userId, cartId);
        String sortKey = getSortKey(userId);
        Boolean delete = redisUntil.delete(cartKey);
        Long count = redisUntil.deleteZSetOneFiled(sortKey, cartId);
        return delete && count != 0;
    }

    public void deleteAllCart(Long userId){
        String key = rCP.getCartCache().getCartPrefix()+ ":" + userId + ":*";
        redisUntil.deleteAllCart(key);
        redisUntil.delete(getSortKey(userId));
    }

    public Set<ZSetOperations.TypedTuple<Object>> getCartAllIds(Long userId,Long lastId,int pageSize){
        String sortKey = getSortKey(userId);
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisUntil.ZSetReverseRangeScore(
                sortKey,
                lastId == null ? Double.POSITIVE_INFINITY : lastId,
                0,
                pageSize + 1
        );

        if (CollectionUtils.isEmpty(typedTuples)){
            log.info("获取的Cart--ZSet缓存为null/空(未命中)");
            return null;
        }
        return typedTuples;
    }


    public List<CartVO> getCartInfoOptimized(Set<ZSetOperations.TypedTuple<Object>> typedTuples, Long userId) {
        // 1. 准备数据结构
        List<CartVO> cartVos = new ArrayList<>();
        Map<String, CartVO> cartKeyMap = new HashMap<>();
        List<String> allCartKeys = new ArrayList<>();
        List<String> allProductKeys = new ArrayList<>();
        List<String> allSpecKeys = new ArrayList<>();
        Map<String, Long> productIdMap = new HashMap<>();
        Map<String, Long> specIdMap = new HashMap<>();

        // 2. 准备阶段 - 收集所有需要查询的键
        typedTuples.forEach(tuple -> {
            Object cartId = tuple.getValue();
            CartVO cartVO = new CartVO();
            if (cartId != null) {
                cartVO.setId(Long.valueOf(cartId.toString()));
            }
            cartVos.add(cartVO);

            // 构建购物车键
            String cartKey = getCartKey(userId, cartId);
            cartKeyMap.put(cartKey, cartVO);
            allCartKeys.add(cartKey);
        });

        // 3. 批量获取购物车信息
        Map<String, Map<Object, Object>> cartResults = redisUntil.hMultiGetBatch(allCartKeys,
                List.of("productSnowflake", "specSnowflake", "quantity"));

        // 4. 收集产品和规格ID
        cartResults.forEach((cartKey, fields) -> {
            CartVO cartVO = cartKeyMap.get(cartKey);
            if (fields != null && !fields.isEmpty()) {
                Long productId = fields.get("productSnowflake") != null ?
                        Long.parseLong(fields.get("productSnowflake").toString()) : null;
                Long specId = fields.get("specSnowflake") != null ?
                        Long.parseLong(fields.get("specSnowflake").toString()) : null;

                if (productId != null && specId != null) {
                    cartVO.setProductSnowflake(productId);
                    cartVO.setSpecSnowflake(specId);
                    cartVO.setQuantity(fields.get("quantity") != null ?
                            Integer.parseInt(fields.get("quantity").toString()) : 0);

                    // 构建产品和规格键
                    String productKey = rCP.getProductCache().getHashPrefix() + ":" + productId;
                    String specKey = rCP.getProductCache().getSpecDetailPrefix() + ":" + specId;

                    allProductKeys.add(productKey);
                    allSpecKeys.add(specKey);
                    productIdMap.put(productKey, productId);
                    specIdMap.put(specKey, specId);
                }
            }
        });

        // 5. 批量获取产品信息
        Map<String, Map<Object, Object>> productResults = redisUntil.hMultiGetBatch(allProductKeys,
                List.of("name", "coverUrl", "status"));

        // 6. 批量获取规格信息
        Map<String, Map<Object, Object>> specResults = redisUntil.hMultiGetBatch(allSpecKeys,
                List.of("spec", "salePrice"));

        // 7. 组装最终结果
        productResults.forEach((productKey, fields) -> {
            Long productId = productIdMap.get(productKey);
            cartVos.stream()
                    .filter(c -> c.getProductSnowflake() != null && c.getProductSnowflake().equals(productId))
                    .forEach(cartVO -> {
                        if (fields != null) {
                            cartVO.setName(fields.get("name") != null ? fields.get("name").toString() : "");
                            cartVO.setCoverUrl(fields.get("coverUrl") != null ? fields.get("coverUrl").toString() : "");
                            cartVO.setStatus(fields.get("status") != null ?
                                    Integer.parseInt(fields.get("status").toString()) : 0);
                        }
                    });
        });

        specResults.forEach((specKey, fields) -> {
            Long specId = specIdMap.get(specKey);
            cartVos.stream()
                    .filter(c -> c.getSpecSnowflake() != null && c.getSpecSnowflake().equals(specId))
                    .forEach(cartVO -> {
                        if (fields != null) {
                            cartVO.setSpec(fields.get("spec") != null ? fields.get("spec").toString() : "");
                            if (fields.get("salePrice") != null) {
                                try {
                                    cartVO.setPrice(new BigDecimal(fields.get("salePrice").toString()));
                                } catch (NumberFormatException e) {
                                    cartVO.setPrice(BigDecimal.ZERO);
                                }
                            }
                        }
                    });
        });

        return cartVos;
    }


    public CartNewVO getNewCar(Long specSnowFlake){
        String specKey = rCP.getProductCache().getSpecDetailPrefix() + ":" + specSnowFlake;
        List<Object> result = redisUntil.hMultiGet(specKey, List.of("spec", "salePrice"));
        CartNewVO cartNewVO = new CartNewVO();
        cartNewVO.setSpec((String) result.get(0));
        cartNewVO.setPrice(BigDecimal.valueOf((Double)result.get(1)));
        return cartNewVO;
    }

    public void addCartOne(CartAddDTO cartAddDTO,Long productSnowflake){
        String cartKey = getCartKey(cartAddDTO.getUserId(), cartAddDTO.getCartId());
        String sortKey = getSortKey(cartAddDTO.getUserId());

        redisUntil.hPutMap(cartKey, Map.of(
                "productSnowflake", productSnowflake,
                "specSnowflake", cartAddDTO.getSpecSnowflake(),
                "quantity", cartAddDTO.getQuantity()
        ));

        redisUntil.ZSetAdd(sortKey, cartAddDTO.getCartId(), Double.valueOf(cartAddDTO.getCartId()));
    }

    public void addLackOne(CartAddDTO cartAddDTO){
        String cartKey = getCartKey(cartAddDTO.getUserId(), cartAddDTO.getCartId());
        Object quantity = redisUntil.hGet(cartKey, "quantity");
        quantity = cartAddDTO.getQuantity() + (Integer) quantity;
        redisUntil.hPutMap(cartKey, Map.of(
                "quantity",quantity
        ));
    }

    public void updateCart(CartUpdateDTO cartUpdateDTO){
        String cartKey = getCartKey(cartUpdateDTO.getUserId(), cartUpdateDTO.getCartId());
        if (cartUpdateDTO.getSpecSnowflake() == null || cartUpdateDTO.getQuantity() != null){
            redisUntil.hPutMap(cartKey, Map.of(
                    "quantity", cartUpdateDTO.getQuantity()
            ));
        }else if (cartUpdateDTO.getSpecSnowflake() != null || cartUpdateDTO.getQuantity() == null){
            redisUntil.hPutMap(cartKey, Map.of(
                    "specSnowflake", cartUpdateDTO.getSpecSnowflake()
            ));
        }else{
            redisUntil.hPutMap(cartKey, Map.of(
                    "specSnowflake", cartUpdateDTO.getSpecSnowflake(),
                    "quantity", cartUpdateDTO.getQuantity()
            ));
        }
    }





}
