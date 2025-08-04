package com.zhuxi.service.Tx;


import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.mapper.PayMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.Pay.PayDTO;

@Slf4j
@Service
public class PayTxService {
    private final PayMapper payMapper;

    public PayTxService(PayMapper payMapper) {
        this.payMapper = payMapper;
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void pay(PayDTO payDTO){
        String OrderSn = payDTO.getOrderSn();
        Long userId = payDTO.getUserId();
        Long orderId = payMapper.getOrderIdByOrderSn(OrderSn);
        if (orderId == null){
            throw new transactionalException(MessageReturn.ORDER_NOT_EXIST);
        }

        int i = payMapper.updateOrderStatus(orderId, userId);
        if (i != 1){
            throw new transactionalException(MessageReturn.UPDATE_ORDER_ERROR);
        }

        int j = payMapper.updateInventory(orderId);
        if (j != 1){
            throw new transactionalException(MessageReturn.INVENTORY_LOCK_UPDATE_ERROR);
        }

        payDTO.setOrderId(orderId);
        int k = payMapper.updatepayment(payDTO);
        if (k != 1){
            throw new transactionalException(MessageReturn.PAYMENT_UPDATE_ERROR);

        }

        Long specId = payMapper.getSpecIdByOrderId(orderId);
        if (specId == null){
            throw new transactionalException(MessageReturn.SPEC_NOT_EXIST);
        }

        int i1 = payMapper.updateRealStock(specId, payDTO.getQuantity());
        if (i1 != 1){
            throw new transactionalException(MessageReturn.REAL_STOCK_UPDATE_ERROR);
        }
    }
}
