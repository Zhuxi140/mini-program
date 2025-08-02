package com.zhuxi.ApplicationRunner.Data.Loader;

import com.zhuxi.service.Cache.ProductRedisCache;
import com.zhuxi.service.Tx.ProductTxService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import src.main.java.com.zhuxi.pojo.DTO.product.PIdSnowFlake;
import src.main.java.com.zhuxi.pojo.DTO.product.SpecRedisDTO;

import java.util.List;

@Component
public class SpecDataLoader {

    private final ProductTxService productTxService;
    private final ProductRedisCache productRedisCache;

    @Value("${init-Data.page-size}")
    private int PageSize;

    public SpecDataLoader(ProductTxService productTxService, ProductRedisCache productRedisCache) {
        this.productTxService = productTxService;
        this.productRedisCache = productRedisCache;
    }


    public void initializeData() {
        syncData();
    }

    public void syncData(){
        Long lastId = 0L;
        int batchSize = PageSize;
        while(true){
            List<PIdSnowFlake> saleProduct = productTxService.getSaleProductId(lastId, batchSize + 1);
            List<Long> productIds = saleProduct.stream()
                    .map(PIdSnowFlake::getId)
                    .toList();
            List<SpecRedisDTO> spec = productTxService.getSpec(productIds);
            if (spec.size() == batchSize + 1){
                lastId = productIds.get(batchSize);
                spec = spec.subList(0, batchSize);
                saleProduct = saleProduct.subList(0, batchSize);
                productRedisCache.syncSpecInit(spec,saleProduct);
            }else {
                productRedisCache.syncSpecInit(spec,saleProduct);
                break;
            }
        }
    }
}
