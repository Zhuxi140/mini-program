package com.zhuxi.service.Impl;

import com.zhuxi.Constant.Message;
import com.zhuxi.Result.ProductPageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.service.ProductService;
import com.zhuxi.service.RedisCache.ProductRedisCache;
import com.zhuxi.service.TxService.ProductTxService;
import com.zhuxi.utils.RedisUntil;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductOverviewVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductSpecVO;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
         * 获取商品列表(默认序列)
         */
        @Override
        public Result<ProductPageResult<ProductOverviewVO>> getListProducts(Double lastScore, Integer pageSize, Integer type,Long lastId) {

            boolean first = (lastScore == null || lastScore < 0);
            String CacheKey = "";
            if (type == 1){
                CacheKey= productRedisCache.getSortCreateDesc();
            }else if(type == 2 || type == 3){
                CacheKey= productRedisCache.getSortPriceKey();
            }
            if (first){
                lastScore = 0D;
            }

            Set<ZSetOperations.TypedTuple<Object>> listIds = productRedisCache.getListProductsIds(CacheKey, type, lastScore, pageSize + 1);
            if (listIds != null){
                Double lastScore1 = productRedisCache.getLastScore(listIds);
                List<ProductOverviewVO> listProducts = productRedisCache.getListProductsCache(listIds,pageSize);
                if (listProducts != null){
                    ProductOverviewVO productOverviewVO = listProducts.get(listProducts.size() - 1);
                    Long id = productOverviewVO.getId();
                    boolean hasPrevious = !first;
                    boolean hasMore = listProducts.size() > pageSize;
                    ProductPageResult<ProductOverviewVO> PageResult = new ProductPageResult<>(listProducts, lastScore1, hasPrevious, hasMore,id);
                    return Result.success(Message.OPERATION_SUCCESS, PageResult);
                }
            }

            ProductPageResult<ProductOverviewVO> objectProductPageResult = new ProductPageResult<>(null, null, false, false, null);
            switch ( type){
                case 1:{
                    long epochMilli = (long) (lastScore * 1000.0 + lastId*10000000);
                    LocalDateTime localDateTime = Instant.ofEpochMilli(epochMilli).atZone(ZoneId.systemDefault()).toLocalDateTime();
                    List<ProductOverviewVO> listProducts = productTxService.getListProductsByCreate(localDateTime, pageSize + 1);
                    productRedisCache.syncProduct(listProducts);
                    objectProductPageResult.setItems(listProducts);
                    objectProductPageResult.setLastId(listProducts.get(listProducts.size() - 1).getId());
                }
                break;
                case 2:{
                    BigDecimal price = restorePrice(lastScore, lastId);
                    List<ProductOverviewVO> listProducts = productTxService.getListProductByPriceDESC(price, pageSize + 1);
                    productRedisCache.syncProduct(listProducts);
                    objectProductPageResult.setItems(listProducts);
                    objectProductPageResult.setLastId(listProducts.get(listProducts.size() - 1).getId());
                }
                break;
                case 3:{
                    BigDecimal price = restorePrice(lastScore, lastId);
                    List<ProductOverviewVO> listProducts = productTxService.getListProductByPriceASC(price, pageSize + 1);
                    productRedisCache.syncProduct(listProducts);
                    objectProductPageResult.setItems(listProducts);
                    objectProductPageResult.setLastId(listProducts.get(listProducts.size() - 1).getId());
                }
                break;
            }

            return Result.success(objectProductPageResult);
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




    private BigDecimal restorePrice(double lastScore,long id){
        double offset = (id % 10000) / 10000000000.0;
        double raw = lastScore - offset;

        return new BigDecimal(String.valueOf(raw));
    }


}
