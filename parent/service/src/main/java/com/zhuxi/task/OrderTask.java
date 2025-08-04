package com.zhuxi.task;

import com.zhuxi.service.Cache.OrderRedisCache;
import com.zhuxi.service.Tx.OrderTxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class OrderTask {

    private final TaskScheduler taskScheduler;
    private final OrderTxService orderTxService;
    private final OrderRedisCache orderRedisCache;

    public OrderTask(TaskScheduler taskScheduler, OrderTxService orderTxService, OrderRedisCache orderRedisCache) {
        this.taskScheduler = taskScheduler;
        this.orderTxService = orderTxService;
        this.orderRedisCache = orderRedisCache;
    }

    public void onMqFailure(String orderSn){
        taskScheduler.schedule(
                ()-> cancelOrder(orderSn), Instant.now().plusSeconds(1800)
                );
    }

    @Transactional
    public void cancelOrder(String orderSn){
        Long orderId = orderRedisCache.getOrderIdBySn(orderSn);
        boolean isHit = (orderId != null);
        orderTxService.concealOrderL(orderId, orderSn, isHit);
    }

}
