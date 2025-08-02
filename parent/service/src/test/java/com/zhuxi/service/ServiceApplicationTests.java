package com.zhuxi.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.zhuxi.mapper.snowflake;
import com.zhuxi.service.Cache.ProductRedisCache;
import com.zhuxi.service.Tx.ProductTxService;
import com.zhuxi.utils.IdSnowFLake;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.List;


@SpringBootTest
class ServiceApplicationTests {

   private ProductRedisCache productRedisCache;
   private ProductTxService productTxService;

   @Autowired
    ServiceApplicationTests(ProductRedisCache productRedisCache, ProductTxService productTxService) {
        this.productRedisCache = productRedisCache;
        this.productTxService = productTxService;
    }

    @Test
    void contextLoads() {

        Long productSnowFlakeById = productTxService.getProductSnowFlakeById(24L);
        List<Long> snowFlakeId = productTxService.getSpecSnowFlakeByIdList(24L);
        productRedisCache.deleteProduct(productSnowFlakeById,snowFlakeId);

    }






}
