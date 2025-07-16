package com.zhuxi.service.RedisCache;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.zhuxi.Result.PageResult;
import com.zhuxi.utils.JsonUtils;
import com.zhuxi.utils.RedisUntil;
import com.zhuxi.utils.properties.RedisCacheProperties;
import org.springframework.stereotype.Service;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductOverviewVO;


@Service
public class ProductRedisCache {

    private final RedisUntil redisUntil;
    private final RedisCacheProperties redisCacheProperties;

    public ProductRedisCache(RedisUntil redisUntil, RedisCacheProperties redisCacheProperties) {
        this.redisUntil = redisUntil;
        this.redisCacheProperties = redisCacheProperties;
    }

    // 获取商品列表缓存的key
    public String BuildKey(Long lastId, Integer pageSize,boolean first){
        if(first){
            String firstRedisKey = "first";
            return StrUtil.format(redisCacheProperties.getProductCache().getKeyPrefix(),firstRedisKey, pageSize);
        }else{
            return StrUtil.format(redisCacheProperties.getProductCache().getKeyPrefix(),lastId, pageSize);
        }

    }

    // 获取商品列表缓存
    public PageResult<ProductOverviewVO> getListProductsCache(String format){
        String stringValue = redisUntil.getStringValue(format);
        if (stringValue != null){
            return JsonUtils.jsonToObject(stringValue, new TypeReference<PageResult<ProductOverviewVO>>() {});
        }
        return null;
    }

    // 写入商品列表缓存
    public void setProductsToRedis(PageResult<ProductOverviewVO> pageResult,String format){
        String json = JsonUtils.objectToJson(pageResult);
        redisUntil.setStringValue(format,json, redisCacheProperties.getProductCache().getTTL());
    }
}
