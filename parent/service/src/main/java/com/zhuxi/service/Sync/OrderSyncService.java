package com.zhuxi.service.Sync;

import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.Result.Result;
import com.zhuxi.pojo.DTO.Order.*;
import com.zhuxi.service.MessageService.OrderMessage;
import com.zhuxi.service.Rollback.OrderRollback;
import com.zhuxi.service.Tx.OrderTxService;
import com.zhuxi.utils.RedisUntil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class OrderSyncService {


    private final OrderTxService orderTxService;
    private final OrderRollback orderRollback;
    private final OrderMessage orderMessage;
    private final DefaultRedisScript<Integer> redisScript;
    private final RedisUntil redisUntil;
    private final RabbitTemplate rabbitTemplate;
    @Autowired
    private DataSourceTransactionManager transactionManager;


    public OrderSyncService(OrderTxService orderTxService, OrderRollback orderRollback, OrderMessage orderMessage, RedisUntil redisUntil, RabbitTemplate rabbitTemplate) {
        this.orderTxService = orderTxService;
        this.orderRollback = orderRollback;
        this.orderMessage = orderMessage;
        this.redisUntil = redisUntil;
        this.rabbitTemplate = rabbitTemplate;
        this.redisScript = new DefaultRedisScript<Integer>();
        this.redisScript.setScriptSource(
                new ResourceScriptSource(new ClassPathResource("Lua/checkUpadaeStatus.lua"))
        );
        this.redisScript.setResultType(Integer.class);

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

                String addingOrder = "order:adding:" + orderSn;
            Object result = redisUntil.UseLua(redisScript, Collections.singletonList(addingOrder), Collections.emptyList());
            if (result instanceof Integer statusA){
                if (statusA != 1){
                    throw new transactionalException("订单已超时");
                }
            }else if(result instanceof Long statusB){
                if (statusB != 1){
                    throw new transactionalException("订单已超时");
                }
            }
            transactionManager.commit(status);
            OrderMqDTO orderMqDTO = new OrderMqDTO(orderSn, userId);
            orderMessage.sendOrderDelayMessage(orderMqDTO, "delay.exchange", "new");
            rabbitTemplate.convertAndSend("order.exchange", "neww", orderMqDTO,
                    message -> {
                        MessageProperties messageProperties = message.getMessageProperties();
                        messageProperties.setMessageId(UUID.randomUUID().toString());
                        messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        return message;
                    }
                    );
                }catch (Exception e){
                log.error("订单创建失败 : {}", e.getMessage());
                        orderRollback.rollbackOrder(orderSn, specId);
                    if (status != null && !status.isCompleted()) {
                        transactionManager.rollback(status);
                    }
                    log.error("-------订单创建失败%n {} ", e.getMessage());
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

    @Transactional
    public Result<String> deductStoddckWithLock(Long specId, Integer productQuantity){

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



    @Async("orderAsyncExecutor")
    public void syncOrderGroups(OrderGroupDTO orderGroupDTO1, List<OrderAddDTO> orderAddDTOS,
                                List<PaymentAddDTO> paymentAddDTOS,List<InventoryLockAddDTO> inventoryLockAddDTOS,
                                List<Long> specIds,List<Integer> quantityList,String groupTarget,
                                List<String> orderSnList
    ) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(Propagation.REQUIRES_NEW.value());
        TransactionStatus status = null;
        try {
            status = transactionManager.getTransaction(def);
            orderTxService.insertOrderGroupList(orderGroupDTO1);

            for (OrderAddDTO addDTO : orderAddDTOS) {
                addDTO.setGroupId(orderGroupDTO1.getId());
            }
            // 创建订单
            orderTxService.insertOrderList(orderAddDTOS);
            for(int i = 0; i < paymentAddDTOS.size(); i++){
                Long id = orderAddDTOS.get(i).getId();
                paymentAddDTOS.get(i).setOrderId(id);
                inventoryLockAddDTOS.get(i).setOrderId(id);
            }
            // 创建支付记录和锁库存
            orderTxService.insertPaymentList(paymentAddDTOS);
            orderTxService.insertInventoryLockList(inventoryLockAddDTOS);
            orderTxService.releaseLockStockList(specIds, quantityList);
            String addingOrder = "order:adding:group:" + groupTarget;
            Object result = redisUntil.UseLua(redisScript, Collections.singletonList(addingOrder), Collections.emptyList());
            if (result instanceof Integer statusA){
                if (statusA != 1){
                    throw new transactionalException("订单已超时");
                }
            }else if(result instanceof Long statusB){
                if (statusB != 1){
                    throw new transactionalException("订单已超时");
                }
            }
            transactionManager.commit(status);
            for (String orderSn : orderSnList){
                OrderMqDTO orderMqDTO = new OrderMqDTO(orderSn, orderAddDTOS.get(0).getUserId());
                orderMessage.sendOrderDelayMessage(orderMqDTO, "delay.exchange", "new");
                rabbitTemplate.convertAndSend("order.exchange", "neww", orderMqDTO,
                        message -> {
                            MessageProperties messageProperties = message.getMessageProperties();
                            messageProperties.setMessageId(UUID.randomUUID().toString());
                            messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                            return message;
                        }
                );
            }

        }catch (Exception e){
            log.info("订单创建失败 : {}", e.getMessage());
            for (int i = 0; i < orderSnList.size(); i++){
                orderRollback.rollbackOrder(orderSnList.get(i), orderAddDTOS.get(i).getSpecId());
            }
            if (status != null && !status.isCompleted()) {
                transactionManager.rollback(status);
            }
            log.warn("-------订单创建失败%n {} ", e.getMessage());
        }finally {
            for (String orderSn : orderSnList){
                orderRollback.deleteKey(orderSn);
            }
            if (status != null && !status.isCompleted()){
                transactionManager.rollback(status);
            }
        }
    }




}
