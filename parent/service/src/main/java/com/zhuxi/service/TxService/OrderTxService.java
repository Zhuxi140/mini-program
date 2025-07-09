package com.zhuxi.service.TxService;

import com.zhuxi.Constant.Message;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.BatchResult;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.Order.InventoryLockAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.Order.OrderAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.Order.OrderGroupDTO;
import src.main.java.com.zhuxi.pojo.DTO.Order.PaymentAddDTO;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class OrderTxService {

    private final OrderMapper orderMapper;
    private final SqlSessionTemplate sqlSessionTemplate;

    public OrderTxService(OrderMapper orderMapper, SqlSessionTemplate sqlSessionTemplate) {
        this.orderMapper = orderMapper;
        this.sqlSessionTemplate = sqlSessionTemplate;
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
        if(defaultAddressId == null || defaultAddressId < 0)
            throw new transactionalException(Message.SELECT_ERROR);
        return defaultAddressId;
    }

    @Transactional(readOnly = true)
    public Integer getProductRealStock(Long specId) {
        Integer productRealStock = orderMapper.getProductRealStock(specId);
        if(productRealStock == null || productRealStock <= 0)
            throw new transactionalException(Message.REAL_STOCK_NOT_EXIST +"/" + Message.SELECT_ERROR);
        return productRealStock;
    }

    @Transactional(readOnly = true)
    public Integer getProductSaleStock(Long specId) {
        Integer productSaleStock = orderMapper.getProductSaleStock(specId);
        if(productSaleStock == null || productSaleStock < 0)
            throw new transactionalException(Message.SPEC_NOT_EXIST +"/" + Message.SELECT_ERROR);
        return productSaleStock;
    }

    @Transactional(readOnly = true)
    public Integer getProductPreStock(Long specId) {
        Integer productPreStock = orderMapper.getProductPreStock(specId);
        if(productPreStock < 0)
            throw new transactionalException(Message.SELECT_ERROR);
        return productPreStock;
    }

    @Transactional(readOnly = true)
    public List<Integer> getProductRealStockList(List<Long> specIds) {
        List<Integer> productRealStockList = orderMapper.getProductRealStockList(specIds);
        if(productRealStockList.size() != specIds.size())
            throw new transactionalException(Message.SPECID_IS_NO_REAL_STOCK);
        for (Integer productRealStock : productRealStockList) {
            if(productRealStock < 0)
                throw new transactionalException(Message.REAL_STOCK_NOT_EXIST +"/" + Message.SELECT_ERROR);
        }

        return productRealStockList;
    }

    @Transactional(readOnly = true)
    public List<Integer> getProductSaleStockList(List<Long> specIds) {
        List<Integer> productSaleStockList = orderMapper.getProductSaleStockList(specIds);
        if(productSaleStockList.size() != specIds.size())
            throw new transactionalException(Message.SPECID_IS_NO_SALE_STOCK);
        for (Integer productSaleStock : productSaleStockList) {
            if(productSaleStock < 0)
                throw new transactionalException(Message.SPEC_NOT_EXIST +"/" + Message.SELECT_ERROR);
        }
        return productSaleStockList;
    }

    @Transactional(readOnly = true)
    public List<Integer> getProductPreStockList(List<Long> specIds) {
        List<Integer> productPreStockList = orderMapper.getProductPreStockList(specIds);
        for (Integer productPreStock : productPreStockList) {
            if(productPreStock < 0)
                throw new transactionalException(Message.SELECT_ERROR);
        }
        return productPreStockList;
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
    @Transactional(rollbackFor = transactionalException.class)
    public void insertInventoryLock(Long productId,Long specId, Long orderId, Integer quantity) {
        if(orderMapper.insertInventoryLock(productId,specId, orderId, quantity) < 0 )
            throw new transactionalException(Message.INSERT_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void insertOrderGroupList(OrderGroupDTO orderGroupDTO) {
        int i = orderMapper.insertOrderGroup(orderGroupDTO);
        if(i !=  1)
            throw new transactionalException(Message.INSERT_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void insertOrderList(List<OrderAddDTO> orderAddDTO) {
        int i = orderMapper.insertOrderList(orderAddDTO);
        if(i !=  orderAddDTO.size())
            throw new transactionalException(Message.INSERT_ERROR);

        Long fistId = orderMapper.getLastInsertId();
        log.warn("firstId : ---{}---", fistId);
        for (int j = 0; j < orderAddDTO.size(); j++) {
            log.warn("firstId + j: ---{}---", fistId + j);
            orderAddDTO.get(j).setId(fistId + j);
        }

    }

    @Transactional(rollbackFor = transactionalException.class)
    public void insertPaymentList(List<PaymentAddDTO> paymentAddDTO) {
        int i = orderMapper.insertPaymentList(paymentAddDTO);
        if(i !=  paymentAddDTO.size())
            throw new transactionalException(Message.INSERT_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void insertInventoryLockList(List<InventoryLockAddDTO> inventoryLockAddDTOS) {
        int i = orderMapper.insertInventoryLockList(inventoryLockAddDTOS);
        if(i !=  inventoryLockAddDTOS.size())
            throw new transactionalException(Message.INSERT_ERROR);
    }

}
