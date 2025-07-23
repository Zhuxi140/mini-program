package com.zhuxi.service.Sync;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zhuxi.Constant.Message;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.service.Rollback.OrderRollback;
import com.zhuxi.service.TxService.OrderTxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import src.main.java.com.zhuxi.pojo.DTO.Order.OrderAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.Order.PaymentAddDTO;
import java.math.BigDecimal;
import java.util.concurrent.*;

@Slf4j
@Service
public class OrderSyncService {

    private static final ExecutorService ASYNC_POOL = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadFactoryBuilder().setNameFormat("async-pool-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    private final OrderTxService orderTxService;
    private final DataSourceTransactionManager transactionManager;
    private final OrderRollback orderRollback;


    public OrderSyncService(OrderTxService orderTxService, DataSourceTransactionManager transactionManager, OrderRollback orderRollback) {
        this.orderTxService = orderTxService;
        this.transactionManager = transactionManager;
        this.orderRollback = orderRollback;
    }

    public void syncOrder(OrderAddDTO orderAddDTO, Long userId, String pSn, String iSn, Long productId, Long specId, Integer productQuantity, String orderSn, BigDecimal frontPrice) {


        ASYNC_POOL.execute(()->{
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
            def.setTimeout(30);

            TransactionStatus transaction = transactionManager.getTransaction(def);
            try {
                orderAddDTO.setOrderSn(orderSn);

                // 验证地址
                dealAddressId(orderAddDTO, userId);
                // 创建订单
                orderTxService.insert(orderAddDTO);
                Long id = orderAddDTO.getId();
                // 创建支付编号
                PaymentAddDTO paymentAddDTO = new PaymentAddDTO(pSn, userId, id, frontPrice, 0);

                orderTxService.insertPayment(paymentAddDTO);
                orderTxService.insertInventoryLock(productId, specId, id, productQuantity, iSn);
                transactionManager.commit(transaction);
                orderRollback.deleteKey(orderSn);
            }catch (Exception e){
                transactionManager.rollback(transaction);
                log.warn("-------订单创建失败%n {} ", e.getMessage());
                orderRollback.rollbackOrder(orderSn, specId);
            }
        });

    }


    private void dealAddressId(OrderAddDTO orderAddDTO,Long userId){
        if(orderAddDTO.getAddressId() == null){
            orderAddDTO.setAddressId(orderTxService.getDefaultAddressId(userId));
        } else if (orderAddDTO.getAddressId() < 1) {
            throw new transactionalException( Message.ADDRESS_ID_IS_NULL_OR_LESS_THAN_ONE);
        }
    }



}
