package com.zhuxi.task;

import com.zhuxi.service.Cache.OrderRedisCache;
import com.zhuxi.service.Tx.OrderTxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;

@Slf4j
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
        log.info("兜底自动取消订单任务启动");
        Long orderId = orderRedisCache.getOrderIdBySn(orderSn);
        if (orderId == null) {
            orderId = orderTxService.getOrderIdByOrderSn(orderSn);
        }
        int orderStatus = orderTxService.getOrderStatus(orderId);
        if (orderStatus != 0) {
            log.info("订单状态发生变更（非待付款），停止取消");
        }else{
            orderTxService.concealOrderL(orderId);
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status){
                // 事务成功则
                if (status == TransactionSynchronization.STATUS_COMMITTED) {
                    orderRedisCache.syncOrderStatus(orderSn,4);
                }else{
                    log.warn("事务未提交");
                }
            }
        });
    }

}
