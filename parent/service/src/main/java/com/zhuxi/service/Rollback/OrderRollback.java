package com.zhuxi.service.Rollback;

import com.zhuxi.service.Cache.OrderRedisCache;
import com.zhuxi.service.Tx.OrderTxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class OrderRollback {

    private final OrderTxService orderTxService;
    private final OrderRedisCache orderRedisCache;



    public OrderRollback(OrderTxService orderTxService, OrderRedisCache orderRedisCache) {
        this.orderTxService = orderTxService;
        this.orderRedisCache = orderRedisCache;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void rollbackOrder(String orderSn,Long specId){
        log.info("rollbackOrder________________执行中");
        Integer quantity = orderRedisCache.getOrderLock(orderSn);
        if (quantity == null || quantity <= 0){
            // 订单无redis记录 即订单无库存锁
            return;
        }
        orderTxService.releaseProductSaleStock(specId, quantity);
        Long orderId = orderTxService.concealOrder(null,orderSn,false);
        orderTxService.releaseLockStock(orderId);
        orderRedisCache.deleteLockKey(orderSn);
    }

    public void deleteKey(String key){
        orderRedisCache.deleteLockKey(key);
    }
}
