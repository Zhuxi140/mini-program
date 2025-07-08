package com.zhuxi.service.TxService;

import com.zhuxi.Constant.Message;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.mapper.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.Order.OrderAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.Order.PaymentAddDTO;

import java.math.BigDecimal;

@Service
public class OrderTxService {
    private final OrderMapper orderMapper;

    public OrderTxService(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Transactional(readOnly = true)
    public BigDecimal getProductSalePrice(Long productId) {
        BigDecimal productSalePrice = orderMapper.getProductSalePrice(productId);
        if(productSalePrice.compareTo(BigDecimal.ZERO) <= 0)
            throw new transactionalException(Message.SPEC_NOT_EXIST +"/" + Message.SELECT_ERROR);
        return productSalePrice;
    }

    @Transactional(readOnly = true)
    public Long getDefaultAddressId(Long userId) {
        Long defaultAddressId = orderMapper.getDefaultAddressId(userId);
        if(defaultAddressId == null || defaultAddressId <= 0)
            throw new transactionalException(Message.SELECT_ERROR);
        return defaultAddressId;
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void insert(OrderAddDTO orderAddDTO) {
        if(orderMapper.insert(orderAddDTO) < 0 )
            throw new transactionalException(Message.INSERT_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void insertPayment(PaymentAddDTO paymentAddDTO) {
        if(orderMapper.insertPayment(paymentAddDTO) < 0 )
            throw new transactionalException(Message.INSERT_ERROR);
    }
}
