package com.zhuxi.service.Sync;

import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.Result.Result;
import com.zhuxi.service.MessageService.OrderMessage;
import com.zhuxi.service.Rollback.OrderRollback;
import com.zhuxi.service.Tx.OrderTxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import src.main.java.com.zhuxi.pojo.DTO.Order.OrderAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.Order.PaymentAddDTO;
import java.math.BigDecimal;

@Slf4j
@Service
public class OrderSyncService {


    private final OrderTxService orderTxService;
    private final OrderRollback orderRollback;
    private final OrderMessage orderMessage;
    @Autowired
    private DataSourceTransactionManager transactionManager;


    public OrderSyncService(OrderTxService orderTxService, OrderRollback orderRollback, OrderMessage orderMessage) {
        this.orderTxService = orderTxService;
        this.orderRollback = orderRollback;
        this.orderMessage = orderMessage;
    }

    @Async("orderAsyncExecutor")
    public void syncOrder(OrderAddDTO orderAddDTO, Long userId, String pSn, String iSn, Long productId, Long specId, Integer productQuantity, String orderSn, BigDecimal frontPrice) {
        OrderAddDTO localDTO = copyOrderDTO(orderAddDTO);
        localDTO.setOrderSn(orderSn);

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(Propagation.REQUIRES_NEW.value());
        TransactionStatus status = null;
        try {
                status = transactionManager.getTransaction(def);
                // 验证地址

                dealAddressId(localDTO, userId);

                localDTO.setProductId(productId);
                localDTO.setSpecId(specId);
                // 创建订单
                orderTxService.insert(localDTO);
                Long id = localDTO.getId();
                // 创建支付
                PaymentAddDTO paymentAddDTO = new PaymentAddDTO(pSn, userId, id, frontPrice, 0);

                orderTxService.insertPayment(paymentAddDTO);
                orderTxService.insertInventoryLock(productId, specId, id, productQuantity, iSn);
            transactionManager.commit(status);
            orderMessage.sendOrderDelayMessage(orderSn, "delay.exchange", "new");

                }catch (Exception e){
                log.info("订单创建失败 : {}", e.getMessage());
                        orderRollback.rollbackOrder(orderSn, specId);
                    if (status != null && !status.isCompleted()) {
                        transactionManager.rollback(status);
                    }
                    log.warn("-------订单创建失败%n {} ", e.getMessage());
                    }finally {
                    orderRollback.deleteKey(orderSn);
                    if (status != null && !status.isCompleted()){
                        transactionManager.rollback(status);
                    }
                }
    }


    private void dealAddressId(OrderAddDTO orderAddDTO,Long userId){
        if(orderAddDTO.getAddressId() == null){
            orderAddDTO.setAddressId(orderTxService.getDefaultAddressId(userId));
        } else if (orderAddDTO.getAddressId() < 1) {
            throw new transactionalException( MessageReturn.ADDRESS_ID_IS_NULL_OR_LESS_THAN_ONE);
        }
    }

    private OrderAddDTO copyOrderDTO(OrderAddDTO original) {
        OrderAddDTO copy = new OrderAddDTO();
        BeanUtils.copyProperties(original, copy); // 使用Spring BeanUtils
        return copy;
    }



    @Transactional
    public Result<String> deductStockWithLock(Long specId, Integer productQuantity){

        boolean success = orderTxService.reduceProductSaleStock(specId, productQuantity);
        if (!success){
            // 验证够购买数量 是否超出可售库存
            Integer productSaleStock = orderTxService.getProductSaleStock(specId);
            if(productQuantity > productSaleStock){
                return Result.error(MessageReturn.QUANTITY_OVER_SALE_STOCK);
            }
            return Result.error(MessageReturn.BUSY_TRY_AGAIN_LATER);
        }

        return Result.success(MessageReturn.OPERATION_SUCCESS);
    }



}
