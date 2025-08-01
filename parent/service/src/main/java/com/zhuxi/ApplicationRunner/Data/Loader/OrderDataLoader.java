package com.zhuxi.ApplicationRunner.Data.Loader;

import com.zhuxi.service.Cache.OrderRedisCache;
import com.zhuxi.service.Tx.OrderTxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import src.main.java.com.zhuxi.pojo.DTO.Order.OrderRedisDTO;
import java.util.List;

@Slf4j
@Component
public class OrderDataLoader {

    private final OrderTxService orderTxService;
    private final OrderRedisCache orderRedisCache;

    @Value("${init-Data.page-size}")
    private int PageSize;

    public OrderDataLoader(OrderTxService orderTxService, OrderRedisCache orderRedisCache) {
        this.orderTxService = orderTxService;
        this.orderRedisCache = orderRedisCache;
    }

    public void initializeData(){
        long now = System.currentTimeMillis();
        Long userId = 0L;
        int batchSize = PageSize;

        while(true){
            List<Long> userIds = orderTxService.getUserIds(userId, batchSize + 1);
            if (userIds.isEmpty()){
                break;
            }
            else if (userIds.size() <= batchSize){
                syncOrderData(userIds);
                break;
            }else {
                userId = userIds.get(userIds.size() - 1);
                userIds = userIds.subList(0, batchSize);
                syncOrderData(userIds);
            }
        }
        log.info("Order数据预加载成功,耗时:{}", System.currentTimeMillis() - now);
    }

    private void syncOrderData(List<Long> userIds){
        int batchSize = PageSize;
        for (Long userIdd : userIds){
            Long orderId = 0L;
            while( true){
                List<OrderRedisDTO> listOrder = orderTxService.getOrderRedisList(userIdd,orderId,batchSize + 1);
                if (listOrder.isEmpty()){
                    break;
                } else if (listOrder.size() <= batchSize){
                    orderRedisCache.syncOrderData(listOrder, userIdd);
                    break;
                }else {
                    orderId = listOrder.get(listOrder.size() - 1).getId();
                    listOrder = listOrder.subList(0, batchSize);
                    orderRedisCache.syncOrderData(listOrder, userIdd);
                }
            }
        }
    }



}
