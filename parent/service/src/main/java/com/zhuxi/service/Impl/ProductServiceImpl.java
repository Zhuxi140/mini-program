package com.zhuxi.service.Impl;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.zhuxi.Constant.Message;
import com.zhuxi.Exception.JwtException;
import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.service.ProductService;
import com.zhuxi.service.RedisCache.ProductRedisCache;
import com.zhuxi.service.TxService.ProductTxService;
import com.zhuxi.utils.JsonUtils;
import com.zhuxi.utils.PageUtils;
import com.zhuxi.utils.RedisUntil;
import com.zhuxi.utils.properties.RedisCacheProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductOverviewVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductSpecVO;

import java.util.*;


@Service
@Log4j2
public class ProductServiceImpl implements ProductService {

    private final ProductTxService productTxService;
    private final ProductRedisCache productRedisCache;
    private final RedisUntil redisUntil;


    public ProductServiceImpl(ProductTxService productTxService, ProductRedisCache productRedisCache, RedisUntil redisUntil) {
        this.productTxService = productTxService;
        this.redisUntil = redisUntil;
        this.productRedisCache = productRedisCache;
    }

    /**
     * 获取商品列表
     */
    @Override
    public Result<PageResult<ProductOverviewVO>> getListProducts(Long lastId, Integer pageSize) {

        boolean first = (lastId == null || lastId < 0);
        String CacheKey = productRedisCache.BuildKey(lastId, pageSize, first);
        PageResult<ProductOverviewVO> listProductsCache = productRedisCache.getListProductsCache(CacheKey);
        if (listProductsCache != null){
            //命中
            return Result.success(Message.OPERATION_SUCCESS, listProductsCache);
        }
        //未命中  查询数据库
        if (first){
            lastId = Long.MAX_VALUE;
        }

        List<ProductOverviewVO> listProducts = productTxService.getListProducts(lastId, pageSize + 1);
        PageResult<ProductOverviewVO> objectPageResult = PageUtils.descPageSelect(listProducts, lastId, pageSize,first);
        productRedisCache.setProductsToRedis(objectPageResult,CacheKey);

        return Result.success(objectPageResult);
    }

    /**
     * 获取商品详情
     */
    @Override
    public Result<ProductDetailVO> getProductDetail(Long id) {

        if(id == null)
            return Result.error(Message.ARTICLE_ID_IS_NULL);

        ProductDetailVO productDetail = productTxService.getProductDetail(id);
        return Result.success(Message.OPERATION_SUCCESS, productDetail);
    }

    /**
     * 获取商品规格
     */
    @Override
    public Result<List<ProductSpecVO>> getProductSpec(Long productId) {
        if (productId == null)
            return Result.error(Message.PRODUCT_ID_IS_NULL);

        List<ProductSpecVO> productSpec = productTxService.getProductSpec(productId);
        return Result.success(Message.OPERATION_SUCCESS, productSpec);
    }




}
