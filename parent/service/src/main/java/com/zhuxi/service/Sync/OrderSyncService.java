package com.zhuxi.service.Sync;

import com.zhuxi.Constant.Message;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.Result.Result;
import com.zhuxi.service.Rollback.OrderRollback;
import com.zhuxi.service.Tx.OrderTxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private DataSourceTransactionManager transactionManager;


    public OrderSyncService(OrderTxService orderTxService,OrderRollback orderRollback) {
        this.orderTxService = orderTxService;
        this.orderRollback = orderRollback;
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
                // 创建订单
                orderTxService.insert(localDTO);
                Long id = localDTO.getId();
                // 创建支付编号
                PaymentAddDTO paymentAddDTO = new PaymentAddDTO(pSn, userId, id, frontPrice, 0);

                orderTxService.insertPayment(paymentAddDTO);
                orderTxService.insertInventoryLock(productId, specId, id, productQuantity, iSn);
                transactionManager.commit(status);
            }catch (Exception e){
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
            throw new transactionalException( Message.ADDRESS_ID_IS_NULL_OR_LESS_THAN_ONE);
        }
    }

    private OrderAddDTO copyOrderDTO(OrderAddDTO original) {
        OrderAddDTO copy = new OrderAddDTO();
        BeanUtils.copyProperties(original, copy); // 使用Spring BeanUtils
        return copy;
    }



    @Transactional
    public Result<String> deductStockWithLock(Long specId, Integer productQuantity){

        // 验证数量
        if(productQuantity == null || productQuantity < 1)
            return Result.error(Message.QUANTITY_IS_NULL_OR_LESS_THAN_ONE);



        boolean success = orderTxService.reduceProductSaleStock(specId, productQuantity);
        if (!success){
            // 验证够购买数量 是否超出可售库存
            Integer productSaleStock = orderTxService.getProductSaleStock(specId);
            if(productQuantity > productSaleStock){
                return Result.error(Message.QUANTITY_OVER_SALE_STOCK);
            }
            return Result.error(Message.BUSY_TRY_AGAIN_LATER);
        }

        return Result.success(Message.OPERATION_SUCCESS);
    }



}
