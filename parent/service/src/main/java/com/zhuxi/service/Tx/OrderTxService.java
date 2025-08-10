package com.zhuxi.service.Tx;

import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Exception.MQException;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import src.main.java.com.zhuxi.pojo.DTO.Order.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class OrderTxService {

    private final OrderMapper orderMapper;
    private final TransactionTemplate transactionTemplate;
    private final SqlSessionFactory sqlSessionFactory;

    public OrderTxService(OrderMapper orderMapper, TransactionTemplate transactionTemplate, SqlSessionFactory sqlSessionFactory) {
        this.orderMapper = orderMapper;
        this.transactionTemplate = transactionTemplate;
        this.sqlSessionFactory = sqlSessionFactory;
    }


    @Transactional(readOnly = true)
    public Long getOrderId(String orderSn) {
        Long orderId = orderMapper.getOrderId(orderSn);
        if(orderId == null || orderId < 0) {
            throw new transactionalException(MessageReturn.ORDER_NOT_EXIST);
        }
        return orderId;
    }

    @Transactional(readOnly = true)
    public Long getOrderIdDelete(String orderSn) {
        Long orderId = orderMapper.getOrderIdDelete(orderSn);
        if(orderId == null || orderId < 0) {
            throw new transactionalException(MessageReturn.ORDER_NOT_EXIST);
        }
        return orderId;
    }

    @Transactional(readOnly = true)
    public Long getProductIdBySnowFlake(Long specSnowFlake){
        Long productId = orderMapper.getProductIdBySnowFlake(specSnowFlake);
        if(productId == null || productId < 0) {
            throw new transactionalException(MessageReturn.SELECT_ERROR);
        }
        return productId;
    }

    @Transactional(readOnly = true)
    public Long getSpecBySnowFlake(Long specSnowFlake){
        Long specId = orderMapper.getSpecIdBySnowFlake(specSnowFlake);
        if(specId == null || specId < 0) {
            throw new transactionalException(MessageReturn.SELECT_ERROR);
        }
        return specId;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<Long> getUserIds(Long lastId,int pageSize){
        List<Long> userIds = orderMapper.getUserIds(lastId, pageSize);
        if(userIds == null || userIds.size() <= 0) {
            return Collections.emptyList();
        }
        return userIds;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<OrderRedisDTO> getOrderRedisList(Long userId,Long lastId,int pageSize) {
        List<OrderRedisDTO> orderRedisList = orderMapper.getOrderRedisList(userId,lastId,pageSize);
        if(orderRedisList == null || orderRedisList.size() <= 0) {
            return Collections.emptyList();
        }
        return orderRedisList;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<OrderRedisDTO> getOrderList(Long userId, LocalDateTime createdAt, Integer pageSize) {
        List<OrderRedisDTO> orderList = orderMapper.getOrderList(userId, createdAt, pageSize);
        if(orderList == null || orderList.size() <= 0) {
            throw new transactionalException(MessageReturn.NO_ORDER_RECORD);
        }
        return orderList;
    }

    public List<BloomOrderDTO> getAllOrderId(Long lastId, int pageSize){
        return orderMapper.getAllOrderId(lastId,pageSize);
    }


    @Transactional(readOnly = true)
    public BigDecimal getProductSalePrice(Long productId) {
        BigDecimal productSalePrice = orderMapper.getProductSalePrice(productId);
        if(productSalePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new transactionalException(MessageReturn.SPEC_NOT_EXIST + "/" + MessageReturn.SELECT_ERROR);
        }
        return productSalePrice;
    }

    @Transactional(readOnly = true)
    public Long getDefaultAddressId(Long userId) {
        Long defaultAddressId = orderMapper.getDefaultAddressId(userId);
        if(defaultAddressId == null || defaultAddressId < 0) {
            throw new transactionalException(MessageReturn.SELECT_ERROR);
        }
        return defaultAddressId;
    }

    @Transactional(readOnly = true)
    public Integer getProductRealStock(Long specId) {
        Integer productRealStock = orderMapper.getProductRealStock(specId);
        if(productRealStock == null || productRealStock <= 0) {
            throw new transactionalException(MessageReturn.REAL_STOCK_NOT_EXIST + "/" + MessageReturn.SELECT_ERROR);
        }
        return productRealStock;
    }

    @Transactional(readOnly = true)
    public Integer getProductSaleStock(Long specId) {
        Integer productSaleStock = orderMapper.getProductSaleStock(specId);
        if(productSaleStock == null || productSaleStock < 0) {
            throw new transactionalException(MessageReturn.SPEC_NOT_EXIST + "/" + MessageReturn.SELECT_ERROR);
        }
        return productSaleStock;
    }

    @Transactional(readOnly = true)
    public Integer getProductPreStock(Long specId) {
        Integer productPreStock = orderMapper.getProductPreStock(specId);
        if(productPreStock < 0) {
            throw new transactionalException(MessageReturn.SELECT_ERROR);
        }
        return productPreStock;
    }

    @Transactional(readOnly = true)
    public List<Integer> getProductRealStockList(List<Long> specIds) {
        List<Integer> productRealStockList = orderMapper.getProductRealStockList(specIds);
        if(productRealStockList.size() != specIds.size()) {
            throw new transactionalException(MessageReturn.SPECID_IS_NO_REAL_STOCK);
        }
        for (Integer productRealStock : productRealStockList) {
            if(productRealStock < 0) {
                throw new transactionalException(MessageReturn.REAL_STOCK_NOT_EXIST + "/" + MessageReturn.SELECT_ERROR);
            }
        }

        return productRealStockList;
    }

    @Transactional(readOnly = true)
    public List<Integer> getProductSaleStockList(List<Long> specIds) {
        List<Integer> productSaleStockList = orderMapper.getProductSaleStockList(specIds);
        if(productSaleStockList.size() != specIds.size()) {
            throw new transactionalException(MessageReturn.SPECID_IS_NO_SALE_STOCK + "或" + MessageReturn.NO_THIS_PRODUCT);
        }
        for (Integer productSaleStock : productSaleStockList) {
            if(productSaleStock < 0) {
                throw new transactionalException(MessageReturn.SPEC_NOT_EXIST + "/" + MessageReturn.SELECT_ERROR);
            }
        }
        return productSaleStockList;
    }

    @Transactional(readOnly = true)
    public List<Integer> getProductPreStockList(List<Long> specIds) {
        List<Integer> productPreStockList = orderMapper.getProductPreStockList(specIds);
        for (Integer productPreStock : productPreStockList) {
            if(productPreStock < 0) {
                throw new transactionalException(MessageReturn.SELECT_ERROR);
            }
        }
        return productPreStockList;
    }


    @Transactional(rollbackFor = transactionalException.class)
    public void insert(OrderAddDTO orderAddDTO) {
        if(orderMapper.insert(orderAddDTO) < 0 ) {
            throw new transactionalException(MessageReturn.INSERT_ERROR);
        }
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void insertPayment(PaymentAddDTO paymentAddDTO) {
        if(orderMapper.insertPayment(paymentAddDTO) < 0 ) {
            throw new transactionalException(MessageReturn.INSERT_ERROR);
        }
    }
    @Transactional(rollbackFor = transactionalException.class)
    public void insertInventoryLock(Long productId,Long specId, Long orderId, Integer quantity,String lockSn) {
        if(orderMapper.insertInventoryLock(productId,specId, orderId, quantity,lockSn) < 0 ) {
            throw new transactionalException(MessageReturn.INSERT_ERROR);
        }
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void insertOrderGroupList(OrderGroupDTO orderGroupDTO) {
        int i = orderMapper.insertOrderGroup(orderGroupDTO);
        if(i !=  1) {
            throw new transactionalException(MessageReturn.INSERT_ERROR);
        }
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void insertOrderList(List<OrderAddDTO> orderAddDTO) {
        int i = orderMapper.insertOrderList(orderAddDTO);
        if(i !=  orderAddDTO.size()){
            throw new transactionalException(MessageReturn.INSERT_ERROR);
        }

        Long fistId = orderMapper.getLastInsertId();
        for (int j = 0; j < orderAddDTO.size(); j++) {
            orderAddDTO.get(j).setId(fistId + j);
        }

    }

    @Transactional(rollbackFor = transactionalException.class)
    public void insertPaymentList(List<PaymentAddDTO> paymentAddDTO) {
        int i = orderMapper.insertPaymentList(paymentAddDTO);
        if(i !=  paymentAddDTO.size()) {
            throw new transactionalException(MessageReturn.INSERT_ERROR);
        }
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void insertInventoryLockList(List<InventoryLockAddDTO> inventoryLockAddDTOS) {
        int i = orderMapper.insertInventoryLockList(inventoryLockAddDTOS);
        if(i !=  inventoryLockAddDTOS.size()) {
            throw new transactionalException(MessageReturn.INSERT_ERROR);
        }
    }


    @Transactional(rollbackFor = transactionalException.class)
    public Long concealOrder(Long orderId,String orderSn,boolean isHit){
        if (!isHit){
            orderId = orderMapper.getOrderId(orderSn);
            if (orderId == null || orderId <= 0){
                throw new transactionalException(MessageReturn.SELECT_ERROR);
            }
        }

        int i1 = orderMapper.cancelOrder(orderId);
        if(i1 !=  1) {
            throw new transactionalException(MessageReturn.ORDER_CONCEAL_ERROR);
        }
        int i = orderMapper.cancelPayment(orderId);
        if(i !=  1) {
            throw new transactionalException(MessageReturn.PAY_CONCEAL_ERROR);
        }
        i = orderMapper.releaseInventoryLock(orderId);
        if(i !=  1) {
            throw new transactionalException(MessageReturn.LOCK_CONCEAL_ERROR);
        }
        return orderId;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public int getOrderStatus(Long orderId){
        int orderStatus = orderMapper.getOrderStatus(orderId);

        return orderStatus;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public Long getOrderIdByOrderSn(String orderSn){
         Long orderId = orderMapper.getOrderId(orderSn);
         if (orderId == null || orderId <= 0) {
             throw new transactionalException(MessageReturn.ORDER_NOT_EXIST);
         }
         return orderId;
    }


    public void concealOrderL(Long orderId){

                int i1 = orderMapper.cancelOrder(orderId);
                if (i1 != 1) {
                    throw new MQException(MessageReturn.ORDER_CONCEAL_ERROR);
                }
                int i = orderMapper.cancelPayment(orderId);
                if (i != 1) {
                    throw new MQException(MessageReturn.PAY_CONCEAL_ERROR);
                }

                Integer quantity = orderMapper.getInventoryLockQuantity(orderId);
                if (quantity == null || quantity <= 0) {
                    throw new MQException(MessageReturn.NO_NEED_RECOVERY_STOCK);
                }

                i = orderMapper.releaseInventoryLock(orderId);
                if (i != 1) {
                    throw new MQException(MessageReturn.LOCK_CONCEAL_ERROR);
                }

                Long specId = orderMapper.getSpecId(orderId);
                if (specId == null) {
                    throw new MQException(MessageReturn.NO_SELECT_SPEC_ID);
                }

                int h = orderMapper.releaseProductSaleStock(specId, quantity);
                if (h != 1) {
                    throw new MQException(MessageReturn.RELEASE_SALE_STOCK_ERROR);
                }
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<Long> getOrderIdList(Long groupId){
        List<Long> orderIdList = orderMapper.getOrderIdList(groupId);
        if(orderIdList == null || orderIdList.isEmpty()) {
            throw new transactionalException(MessageReturn.SELECT_ERROR);
        }

        return orderIdList;
    }

    public void concealOrderList(List<Long> orderIds) {
        final int BATCH_SIZE = 50;
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            OrderMapper mapper = sqlSession.getMapper(OrderMapper.class);
            int totalBatches = (orderIds.size() + BATCH_SIZE - 1) / BATCH_SIZE;

            for (int i = 0; i < totalBatches; i++) {
                List<Long> batch = orderIds.subList(i * BATCH_SIZE,
                        Math.min((i + 1) * BATCH_SIZE, orderIds.size()));

                mapper.cancelOrderList(batch);
                mapper.cancelPaymentList(batch);
                mapper.releaseInventoryLockList(batch);

                if (i % 10 == 0) {
                    sqlSession.flushStatements();
                }
            }
            List<BatchResult> batchResults = sqlSession.flushStatements();
            validateBatchResults(batchResults, orderIds.size());
            sqlSession.commit();
        } catch (Exception e) {
            throw new transactionalException("批处理失败--:" + e.getMessage());
        }
    }

    @Transactional(rollbackFor = transactionalException.class)
    public boolean reduceProductSaleStock(Long specId, Integer quantity) {
        return orderMapper.reduceProductSaleStock(specId, quantity);

    }

    @Transactional(rollbackFor = transactionalException.class)
    public void releaseLockStock(Long orderId){
        int i = orderMapper.getInventoryLockQuantity(orderId);
        if(i <= 0) {
            throw new transactionalException(MessageReturn.NO_NEED_RECOVERY_STOCK);
        }
        Long specId = orderMapper.getSpecId(orderId);
        if (specId == null) {
            throw new transactionalException(MessageReturn.NO_SELECT_SPEC_ID);
        }

        int i1 = orderMapper.releaseProductSaleStock(specId, i);
        if(i1 !=  1) {
            throw new transactionalException(MessageReturn.RELEASE_SALE_STOCK_ERROR);
        }
    }

    @Transactional(rollbackFor = MQException.class)
    public void releaseLockStockL(Long orderId){

    }


    @Transactional(rollbackFor = transactionalException.class)
    public void releaseProductSaleStock(Long specId,Integer quantity){
        int i = orderMapper.releaseProductSaleStock(specId,quantity);
        if(i !=  1) {
            throw new transactionalException(MessageReturn.RELEASE_SALE_STOCK_ERROR);
        }
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void releaseLockStockList(List<Long> specIds, List<Integer> quantityList){
        int i = orderMapper.reduceProductSaleStockList(specIds, quantityList);
        if(i !=  specIds.size()) {
            throw new transactionalException(MessageReturn.REDUCE_SALE_STOCK_ERROR);
        }

    }

    @Transactional(rollbackFor = transactionalException.class)
    public void releaseLockStockList(List<Long> orderIds){
        List<Long> specIdList = orderMapper.getSpecIdList(orderIds);
        if (specIdList == null || specIdList.isEmpty()) {
            throw new transactionalException(MessageReturn.NO_SELECT_SPEC_ID);
        }
        List<Integer> quantityList = orderMapper.getInventoryLockQuantityList(orderIds);
        if (quantityList == null || quantityList.isEmpty()) {
            throw new transactionalException(MessageReturn.NO_NEED_RECOVERY_STOCK);
        }
        int i = orderMapper.releaseProductSaleStockList(specIdList, quantityList);
        if(i !=  specIdList.size()) {
            throw new transactionalException(MessageReturn.RELEASE_SALE_STOCK_ERROR);
        }
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void deleteOrder(Long id,Long userId,boolean isHit,String orderSn){
        if (!isHit){
            id = getOrderIdDelete(orderSn);
        }
        int i = orderMapper.deleteOrder(id,userId);
        if(i !=  1) {
            throw new transactionalException(MessageReturn.DELETE_ORDER_ERROR);
        }
    }



    private void validateBatchResults(List<BatchResult> batchResults, int expected) {
        if (batchResults.isEmpty()) {
            throw new transactionalException("无批处理结果");
        }

        int totalUpdateCountsOrder = 0;
        int totalUpdateCountsPayment = 0;
        int totalUpdateCountsInventory = 0;

        for (BatchResult br : batchResults) {
            int[] counts = br.getUpdateCounts();
            if (counts == null || counts.length != 3) {
                throw new transactionalException("批处理操作数量异常");
            }
            totalUpdateCountsOrder += counts[0];
            totalUpdateCountsPayment += counts[1];
            totalUpdateCountsInventory += counts[2];
        }

        if (totalUpdateCountsOrder > expected || totalUpdateCountsPayment > expected || totalUpdateCountsInventory > expected) {
            throw new transactionalException("更新行数超过预期");
        }
    }


}
