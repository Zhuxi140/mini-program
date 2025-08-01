package com.zhuxi.ApplicationRunner.Data.Loader;

import com.zhuxi.service.Cache.ProductRedisCache;
import com.zhuxi.service.Tx.ProductTxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductDetailVO;
import java.util.List;

@Component
@Slf4j
public class ProductDataLoader {

    private final ProductTxService productTxService;
    private final ProductRedisCache productRedisCache;

    @Value("${init-Data.page-size}")
    private int PageSize;

    public ProductDataLoader(ProductTxService productTxService, ProductRedisCache productRedisCache) {
        this.productTxService = productTxService;
        this.productRedisCache = productRedisCache;
    }

    public void initializeData() {
        long now = System.currentTimeMillis();
        Long lastId = 0L;
        int batchSize = PageSize;

        while(true){

            List<ProductDetailVO> listProducts = productTxService.getListProduct(lastId,batchSize + 1);
            if (listProducts == null || listProducts.isEmpty()) {
                break;
            }

            if (listProducts.size() <= batchSize){
                productRedisCache.syncProductInit(listProducts);
                break;
            }else {
                lastId = listProducts.get(listProducts.size() - 1).getId();
                listProducts = listProducts.subList(0, batchSize);
                productRedisCache.syncProductInit(listProducts);
            }
        }
        log.info("Product数据预加载成功,耗时:{}", System.currentTimeMillis() - now);
    }
}
