package com.zhuxi.service.Impl;

import com.zhuxi.Constant.Message;
import com.zhuxi.Result.ProductPageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.service.ProductService;
import com.zhuxi.service.RedisCache.ProductRedisCache;
import com.zhuxi.service.TxService.ProductTxService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductOverviewVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductSpecVO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
@Log4j2
public class ProductServiceImpl implements ProductService {

    private final ProductTxService productTxService;
    private final ProductRedisCache productRedisCache;



    public ProductServiceImpl(ProductTxService productTxService, ProductRedisCache productRedisCache) {
        this.productTxService = productTxService;
        this.productRedisCache = productRedisCache;
    }

        /**
         * 获取商品列表(默认序列)
         */
        @Override
        public Result<ProductPageResult<ProductOverviewVO>> getListProducts(Long lastScore, Integer pageSize, Integer type,Long lastId,Boolean isLast) {
            // 如果为最后Redis最后一页的下一页 直接执行兜底
            if (isLast){
             return noHit(lastScore, pageSize, type, lastId);
            }
            boolean first = (lastScore == null || lastScore < 0);
            String CacheKey = "";
            if (type == 1){
                CacheKey= productRedisCache.getSortCreateDesc();
            }else if(type == 2 || type == 3){
                CacheKey= productRedisCache.getSortPriceKey();
            }
            if (first){
                lastScore = 0L;
            }

            Set<ZSetOperations.TypedTuple<Object>> listIds = productRedisCache.getListProductsIds(CacheKey, type, lastScore, pageSize + 1);
            Long lastScore1 = productRedisCache.getLastScore(listIds);
            if (listIds != null){
                List<ProductOverviewVO> listProducts = productRedisCache.getListProductsCache(listIds,pageSize);
                if (listProducts != null){
                    ProductOverviewVO productOverviewVO = listProducts.get(listProducts.size() - 1);
                    Long id = productOverviewVO.getId();
                    boolean hasMore = listIds.size() > pageSize;
                    ProductPageResult<ProductOverviewVO> PageResult = new ProductPageResult<>(listProducts,lastScore1,hasMore,id);
                    return Result.success(Message.OPERATION_SUCCESS, PageResult);
                }
            }
            log.info("未命中Redis");
            //未命中 启用兜底 查询数据库
            return noHit(lastScore, pageSize, type, lastId);
        }

    /**
     * 获取商品详情
     */
    @Override
    public Result<ProductDetailVO> getProductDetail(Long id) {

        if(id == null)
            return Result.error(Message.ARTICLE_ID_IS_NULL);
        ProductDetailVO cacheDetails = productRedisCache.getCacheDetails(id);
        if (cacheDetails != null){
            return Result.success(Message.OPERATION_SUCCESS, cacheDetails);
        }

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

    private Result<ProductPageResult<ProductOverviewVO>> noHit(Long lastScore, Integer pageSize, Integer type,Long lastId){
        ProductPageResult<ProductOverviewVO> objectProductPageResult = new ProductPageResult<>(null, null, false, null);
        switch ( type){
            case 1:{
                // 恢复时间戳
                LocalDateTime localDateTime = Instant.ofEpochMilli(lastScore).atZone(ZoneId.systemDefault()).toLocalDateTime();
                List<ProductOverviewVO> listProducts = productTxService.getListProductsByCreate(lastId,localDateTime, pageSize + 1);
                if (listProducts == null || listProducts.isEmpty()){
                    return Result.error(Message.NO_DATA,null);
                }
                dealPageResult(pageSize, objectProductPageResult, listProducts, type);
            }
            break;
            case 2:{
                BigDecimal price = restorePrice(lastScore);
                List<ProductOverviewVO> listProducts = productTxService.getListProductByPriceDESC(lastId,price, pageSize + 1);
                if (listProducts == null || listProducts.isEmpty()){
                    return Result.error(Message.NO_DATA,null);
                }
                dealPageResult(pageSize, objectProductPageResult, listProducts, type);
            }
            break;
            case 3:{
                BigDecimal price = restorePrice(lastScore);
                List<ProductOverviewVO> listProducts = productTxService.getListProductByPriceASC(lastId,price, pageSize + 1);
                if (listProducts == null || listProducts.isEmpty()){
                    return Result.error(Message.NO_DATA,null);
                }
                dealPageResult(pageSize, objectProductPageResult, listProducts, type);
            }
            break;
        }

        return Result.success(objectProductPageResult);
    }

    private void dealPageResult(Integer pageSize, ProductPageResult<ProductOverviewVO> objectProductPageResult, List<ProductOverviewVO> listProducts,Integer type) {
        if (listProducts.size() > pageSize){
            listProducts = listProducts.subList(0, pageSize);  // 截取listProducts
        }
        productRedisCache.syncProduct(listProducts);
        ProductOverviewVO productOverviewVO = listProducts.get(listProducts.size() - 1);
        objectProductPageResult.setItems(listProducts);
        Long id = productOverviewVO.getId();
        objectProductPageResult.setLastId(id);
        LocalDateTime createdAt = productOverviewVO.getCreatedAt();
        BigDecimal price = productOverviewVO.getPrice();
        Long processing = processing(type, price, createdAt,id);
        objectProductPageResult.setNextCursor(processing);
    }


    private BigDecimal restorePrice(Long lastScore){
        long priceInt = (lastScore >> 30) & 0x7FFFFF;
        return BigDecimal.valueOf(priceInt).divide(BigDecimal.valueOf(100),2, RoundingMode.HALF_UP);
    }

    private Long processing(Integer type, BigDecimal price, LocalDateTime time, Long id){
        return switch (type) {
            case 1 -> time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            case 2, 3 -> {
                Long priceInt = price.multiply(BigDecimal.valueOf(100)).longValueExact();
                yield productRedisCache.generatePriceId(priceInt, id);
            }
            default -> null;
        };

    }
}
