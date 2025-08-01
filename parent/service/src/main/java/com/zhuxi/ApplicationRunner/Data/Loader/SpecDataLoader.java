package com.zhuxi.ApplicationRunner.Data.Loader;

import com.zhuxi.service.Cache.ProductRedisCache;
import com.zhuxi.service.Tx.ProductTxService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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
            List<SpecRedisDTO> spec = productTxService.getSpec(lastId, batchSize + 1);
            if (spec.size() == batchSize + 1){
                lastId = spec.get(batchSize).getId();
                spec = spec.subList(0, batchSize);
                productRedisCache.syncSpecInit(spec);
            }else {
                productRedisCache.syncSpecInit(spec);
                break;
            }
        }
    }
}
