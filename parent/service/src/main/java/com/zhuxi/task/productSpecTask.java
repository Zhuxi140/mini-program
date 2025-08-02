package com.zhuxi.task;


import com.zhuxi.service.Cache.ProductRedisCache;
import com.zhuxi.service.Tx.ProductTxService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import src.main.java.com.zhuxi.pojo.DTO.product.snowFlakeMap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class productSpecTask {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private ProductTxService productTxService;
    private ProductRedisCache productRedisCache;

    @Value("${init-Data.page-size}")
    private Integer pageSize;

    public productSpecTask(ProductTxService productTxService, ProductRedisCache productRedisCache) {
        this.productTxService = productTxService;
        this.productRedisCache = productRedisCache;
    }


    @Scheduled(cron = "0 0 */11 * * ?")
    public void syncRedisMap()
    {
        Long lastId = 0L;
        while (true){
            List<snowFlakeMap> snowFlakeMap = productTxService.getSnowFlakeMap(lastId, pageSize + 1);
            if (snowFlakeMap.size() == pageSize + 1){
                lastId = snowFlakeMap.get(pageSize).getProductSnowFlake();
                snowFlakeMap = snowFlakeMap.subList(0, pageSize);
            }else {
        /*        productRedisCache*/
                break;
            }

        }
    }

}
