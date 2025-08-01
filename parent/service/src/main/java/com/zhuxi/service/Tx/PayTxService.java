package com.zhuxi.service.Tx;


import com.zhuxi.Constant.Message;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.mapper.PayMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.Pay.PayDTO;

@Service
public class PayTxService {
    private final PayMapper payMapper;

    public PayTxService(PayMapper payMapper) {
        this.payMapper = payMapper;
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void pay(PayDTO payDTO){
        Long orderId = payDTO.getOrderId();
        Long userId = payDTO.getUserId();
        int i = payMapper.updateOrderStatus(orderId, userId);
        if (i != 1)
            throw new transactionalException(Message.UPDATE_ORDER_ERROR);
        int j = payMapper.updateInventory(orderId);
        if (j != 1)
            throw new transactionalException(Message.INVENTORY_LOCK_UPDATE_ERROR);
        int k = payMapper.updatepayment(payDTO);
        if (k != 1)
            throw new transactionalException(Message.PAYMENT_UPDATE_ERROR);
    }
}
