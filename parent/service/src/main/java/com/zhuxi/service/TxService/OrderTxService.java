package com.zhuxi.service.TxService;

import com.zhuxi.Constant.Message;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.Order.InventoryLockAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.Order.OrderAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.Order.OrderGroupDTO;
import src.main.java.com.zhuxi.pojo.DTO.Order.PaymentAddDTO;
import src.main.java.com.zhuxi.pojo.VO.Order.OrderRealShowVO;
import src.main.java.com.zhuxi.pojo.VO.Order.OrderShowVO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OrderTxService {

    private final OrderMapper orderMapper;

    public OrderTxService(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Transactional(readOnly = true)
    public List<OrderShowVO> getOrderList(Long userId,Long lastId, Integer pageSize) {
        List<OrderShowVO> orderList = orderMapper.getOrderList(userId, lastId, pageSize);
        if(orderList == null || orderList.size() <= 0)
            throw new transactionalException(Message.NO_ORDER_RECORD);

        return orderList;
    }

    public List<Long> getAllOrderId(Long lastId,int pageSize){
        return orderMapper.getAllOrderId(lastId,pageSize);
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
            throw new transactionalException(Message.SPECID_IS_NO_SALE_STOCK + "或" + Message.NO_THIS_PRODUCT);
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
        for (int j = 0; j < orderAddDTO.size(); j++) {
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

    @Transactional(rollbackFor = transactionalException.class)
    public void concealOrder(Long orderId){
        int i = orderMapper.cancelOrder(orderId);
        log.warn("concealOrder: ---{}---", i);
        if(i !=  1)
            throw new transactionalException(Message.ORDER_CONCEAL_ERROR);
        i = orderMapper.cancelPayment(orderId);
        if(i !=  1)
            throw new transactionalException(Message.PAY_CONCEAL_ERROR);
        i = orderMapper.releaseInventoryLock(orderId);
        if(i !=  1)
            throw new transactionalException(Message.LOCK_CONCEAL_ERROR);
    }

    @Transactional(readOnly = true)
    public List<Long> getOrderIdList(Long groupId){
        List<Long> orderIdList = orderMapper.getOrderIdList(groupId);
        if(orderIdList == null || orderIdList.isEmpty())
            throw new transactionalException(Message.SELECT_ERROR);

        return orderIdList;
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void concealOrderList(List<Long> orderIds){
        int i = orderMapper.cancelOrderList(orderIds);
        if(i !=  orderIds.size())
            throw new transactionalException(Message.ORDER_CONCEAL_ERROR);
        i = orderMapper.cancelPaymentList(orderIds);
        if(i !=  orderIds.size())
            throw new transactionalException(Message.PAY_CONCEAL_ERROR);
        i = orderMapper.releaseInventoryLockList(orderIds);
        if(i !=  orderIds.size())
            throw new transactionalException(Message.LOCK_CONCEAL_ERROR);

    }

    @Transactional(rollbackFor = transactionalException.class)
    public void reduceProductSaleStock(Long specId, Integer quantity) {
        int i = orderMapper.reduceProductSaleStock(specId, quantity);
        if(i !=  1)
            throw new transactionalException(Message.REDUCE_SALE_STOCK_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void releaseLockStock(Long orderId){
        int i = orderMapper.getInventoryLockQuantity(orderId);
        if(i <= 0)
            throw new transactionalException(Message.NO_NEED_RECOVERY_STOCK);
        Long specId = orderMapper.getSpecId(orderId);
        if (specId == null)
            throw new transactionalException(Message.NO_SELECT_SPEC_ID);

        int i1 = orderMapper.releaseProductSaleStock(specId, i);
        if(i1 !=  1)
            throw new transactionalException(Message.RELEASE_SALE_STOCK_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void releaseLockStockList(List<Long> specIds, List<Integer> quantityList){
        int i = orderMapper.reduceProductSaleStockList(specIds, quantityList);
        if(i !=  specIds.size())
            throw new transactionalException(Message.REDUCE_SALE_STOCK_ERROR);

    }

    @Transactional(rollbackFor = transactionalException.class)
    public void releaseLockStockList(List<Long> orderIds){
        List<Long> specIdList = orderMapper.getSpecIdList(orderIds);
        if (specIdList == null || specIdList.isEmpty())
            throw new transactionalException(Message.NO_SELECT_SPEC_ID);
        List<Integer> quantityList = orderMapper.getInventoryLockQuantityList(orderIds);
        if (quantityList == null || quantityList.isEmpty())
            throw new transactionalException(Message.NO_NEED_RECOVERY_STOCK);
        int i = orderMapper.releaseProductSaleStockList(specIdList, quantityList);
        if(i !=  specIdList.size())
            throw new transactionalException(Message.RELEASE_SALE_STOCK_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void deleteOrder(Long orderId,Long userId){
        int i = orderMapper.deleteOrder(orderId,userId);
        if(i !=  1)
            throw new transactionalException(Message.DELETE_ORDER_ERROR);
    }
}
