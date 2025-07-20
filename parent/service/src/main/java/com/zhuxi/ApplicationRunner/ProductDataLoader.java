package com.zhuxi.ApplicationRunner;

import com.zhuxi.service.RedisCache.ProductRedisCache;
import com.zhuxi.service.TxService.ProductTxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductDetailVO;
import java.util.List;

@Component
@Slf4j
public class ProductDataLoader {

    private final ProductTxService productTxService;
    private final ProductRedisCache productRedisCache;

    public ProductDataLoader(ProductTxService productTxService, ProductRedisCache productRedisCache) {
        this.productTxService = productTxService;
        this.productRedisCache = productRedisCache;
    }

    public void initializeData() {
        Long lastId = 0L;
        int batchSize = 500;

        while(true){

            List<ProductDetailVO> listProducts = productTxService.getListProduct(lastId,batchSize);
            if (listProducts == null || listProducts.isEmpty()){
                break;
            }

            lastId = listProducts.get(listProducts.size() - 1).getId();
            productRedisCache.syncProductInit(listProducts);

            if (listProducts.size() < batchSize){
                break;
            }
        }

    }
}
