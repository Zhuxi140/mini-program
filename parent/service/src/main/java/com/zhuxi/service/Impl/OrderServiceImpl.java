package com.zhuxi.service.Impl;

import com.zhuxi.Constant.Message;
import com.zhuxi.Exception.SnycException;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.service.OrderService;
import com.zhuxi.service.RedisCache.OrderRedisCache;
import com.zhuxi.service.Sync.OrderSyncService;
import com.zhuxi.service.TxService.OrderTxService;
import com.zhuxi.utils.IdSnowFLake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.Order.*;
import src.main.java.com.zhuxi.pojo.VO.Order.OrderRealShowVO;
import src.main.java.com.zhuxi.pojo.VO.Order.OrderShowVO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderTxService orderTxService;
    private final IdSnowFLake idSnowFLake;
    private final OrderRedisCache orderRedisCache;
    private final OrderSyncService orderSyncService;

    public OrderServiceImpl(OrderTxService orderTxService, IdSnowFLake idSnowFLake, OrderRedisCache orderRedisCache, OrderSyncService orderSyncService) {
        this.orderTxService = orderTxService;
        this.idSnowFLake = idSnowFLake;
        this.orderRedisCache = orderRedisCache;
        this.orderSyncService = orderSyncService;
    }

    /**
     * 添加订单
     */
    @Override
    public Result<String> add(OrderAddDTO orderAddDTO,Long userId) {
        // 基础效验
        if (orderAddDTO == null)
            return Result.error(Message.BODY_NO_MAIN_OR_IS_NULL);

        orderAddDTO.setUserId(userId);
        Long productId = orderAddDTO.getProductId();
        Long specId = orderAddDTO.getSpecId();
        if(productId == null || specId == null)
            return Result.error(Message.SPEC_ID_IS_NULL + "或" + Message.PRODUCT_ID_IS_NULL);

        Integer productQuantity = orderAddDTO.getProductQuantity();
        BigDecimal totalAmount = orderAddDTO.getTotalAmount();
        if (totalAmount == null) {
            throw new SnycException(Message.PRICE_IS_NULL);
        }
        BigDecimal productSalePrice = orderTxService.getProductSalePrice(orderAddDTO.getSpecId());
        BigDecimal blackTotalPrice = productSalePrice.multiply(BigDecimal.valueOf(orderAddDTO.getProductQuantity())).setScale(2, RoundingMode.HALF_UP);
        BigDecimal frontPrice = totalAmount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal abs = blackTotalPrice.subtract(frontPrice).abs();
        BigDecimal bigDecimal = new BigDecimal("0.01");
        if (abs.compareTo(bigDecimal) > 0)
            throw new SnycException(Message.PRICE_IS_DIFFERENCE);

        // 预先减库存 （乐观锁）
        Result<String> voidResult = orderSyncService.deductStockWithLock( specId,productQuantity);
        if (voidResult.getCode() == 500){
            return voidResult;
        }

        //生成个业务单号
        String pSn = generateSn(2);
        String orderSn = generateSn(1);
        String iSn = generateSn(4);

        // 将单号临时存储Redis
        orderRedisCache.saveOrderLock(orderSn,productQuantity);

        orderSyncService.syncOrder(orderAddDTO,userId,pSn,iSn,productId,specId,productQuantity,orderSn,frontPrice);

        return Result.success(Message.OPERATION_SUCCESS,orderSn);
    }

    /**
     * 批量添加订单
     */
    @Override
    @Transactional(rollbackFor = {transactionalException.class,RuntimeException.class })
    public Result<Void> addGroup(List<OrderAddDTO> orderAddDTO, Long userId) {
        if(orderAddDTO == null || orderAddDTO.isEmpty())
            return Result.error(Message.BODY_NO_MAIN_OR_IS_NULL);

        List<Long> specIds = new ArrayList<>();

        for(int i = 0; i < orderAddDTO.size(); i++){
            OrderAddDTO order = orderAddDTO.get(i);
            // 验证必填字段(productId、specId、productQuantity、totalAmount)
            validateMustFields( order,i);
            specIds.add(order.getSpecId());
        }

        List<Integer> productSaleStockList = orderTxService.getProductSaleStockList(specIds);
        List<Integer> productPreStockList = orderTxService.getProductPreStockList(specIds);
        List<PaymentAddDTO> paymentAddDTOS = new ArrayList<>();
        List<InventoryLockAddDTO> inventoryLockAddDTOS = new ArrayList<>();
        List<OrderAddDTO> orderAddDTOS = new ArrayList<>();
        List<Integer> quantityList = new ArrayList<>();

        BigDecimal amount = new BigDecimal("0.00");
        for(int i = 0; i < orderAddDTO.size(); i++){
            OrderAddDTO order = orderAddDTO.get(i);
            order.setUserId(userId);

            // 处理地址
            dealAddressId(order,userId,i);
            // 生成订单编号
            order.setOrderSn(generateSn(1));
            // 验证价格
            validatePrice( order,i);
            amount = amount.add(order.getTotalAmount());
            //验证库存
            validateStock(order,i,productSaleStockList,productPreStockList);

            quantityList.add(order.getProductQuantity());
            orderAddDTOS.add( order);
            InventoryLockAddDTO inventoryLockAddDTO = new InventoryLockAddDTO(order.getProductId(), order.getId(), order.getSpecId(), order.getProductQuantity());
            inventoryLockAddDTOS.add(inventoryLockAddDTO);
            PaymentAddDTO paymentAddDTO = new PaymentAddDTO(generateSn(2), userId, order.getId(), order.getTotalAmount(), 0);
            paymentAddDTOS.add(paymentAddDTO);
        }

        // 创建订单组
        OrderGroupDTO orderGroupDTO1 = new OrderGroupDTO(null, generateSn(3), userId, amount, 0);
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

        return Result.success(Message.OPERATION_SUCCESS);
    }

    /**
     * 取消订单
     */
    @Override
    @Transactional
    public Result<Void> cancelOrder(String orderSn, Long userId) {
        if (orderSn == null || userId == null)
            return Result.error(Message.ORDER_USER_ID_IS_NULL);

        Long orderId = orderTxService.concealOrder(orderSn);
        orderTxService.releaseLockStock(orderId);

        return Result.success(Message.OPERATION_SUCCESS);
    }

    /**
     * 取消订单组
     */
    @Override
    @Transactional
    public Result<Void> cancelOrderGroup(Long groupId, Long userId) {
        if (groupId == null)
            return Result.error(Message.GROUP_ID_IS_NULL);
        List<Long> orderIdList = orderTxService.getOrderIdList(groupId);
        orderTxService.concealOrderList(orderIdList);

        return Result.success(Message.OPERATION_SUCCESS);
    }


    /**
     * 获取订单列表
     */
    @Override
    public PageResult<List<OrderRealShowVO>,Double> getOrderList(Long userId, Long lastScore, Integer pageSize,boolean isLast) {
/*        if (isLast){
            //若redis中已经查空 则直接进行兜底
            return getOrdersBySql(userId, lastScore, pageSize);
        }*/

        boolean first = lastScore == null || lastScore < 0;
        if ( first){
            lastScore = 0L;
        }
        boolean hasPrevious = !first;
        boolean hasMore = false;

        Set<ZSetOperations.TypedTuple<Object>> orderSns = orderRedisCache.getOrderSns(userId, lastScore, pageSize + 1);
        if (orderSns != null){
            //命中redis
            List<OrderRealShowVO> orderRealShowVos = orderRedisCache.getOrderRealShowVals(orderSns);
            if (orderRealShowVos.size() == pageSize + 1){
                hasMore = true;
                lastScore = orderRedisCache.getLastScores(orderRealShowVos);
                orderRealShowVos = orderRealShowVos.subList(0, pageSize);
            }else{
                lastScore = orderRedisCache.getLastScores(orderRealShowVos);
            }
            return new PageResult(orderRealShowVos, lastScore, hasPrevious, hasMore);
        }

        //未命中 直接启动兜底
        /*return getOrdersBySql(userId, lastScore, pageSize);*/
        return null;
    }
    /**
     * 删除订单
     */
    @Override
    @Transactional
    public Result<Void> deleteOrder(String orderSn, Long userId) {
        if (orderSn == null || userId == null)
            return Result.error(Message.ORDER_USER_ID_IS_NULL);

        orderTxService.deleteOrder(orderSn,userId);

        return Result.success(Message.OPERATION_SUCCESS);
    }


    // 生成订单/支付编号(雪花算法）
    private String generateSn(Integer  type){
        if(type.equals(1)){
            long l = idSnowFLake.getIdInt();
            return String.format("%s%d","OD",l);
        }else if (type.equals(2)){
            long l = idSnowFLake.getIdInt();
            return String.format("%s%d","PAY", l);
        } else if (type.equals(3)) {
            long l = idSnowFLake.getIdInt();
            return String.format("%s%d","GROUP", l);
        }else if(type.equals(4)){
            long l = idSnowFLake.getIdInt();
            return String.format("%s%d","ITY", l);
        }

        throw new RuntimeException(Message.ID_GENERATE_ERROR);
    }

    // 验证必填字段
    private void validateMustFields(OrderAddDTO orderAddDTO, int i){
        if(orderAddDTO.getProductId() == null
           || orderAddDTO.getSpecId() == null
           || orderAddDTO.getProductQuantity() == null
           || orderAddDTO.getTotalAmount() == null
           || orderAddDTO.getProductQuantity() < 1
        )
            throw new transactionalException("订单"+ i + ": " + Message.FIELDS_IS_NULL + "/" + Message.QUANTITY_IS_NULL_OR_LESS_THAN_ONE);
    }

    // 处理地址
    private void dealAddressId(OrderAddDTO orderAddDTO,Long userId,int i){
        if(orderAddDTO.getAddressId() == null){
            orderAddDTO.setAddressId(orderTxService.getDefaultAddressId(userId));
        } else if (orderAddDTO.getAddressId() < 1) {
            throw new transactionalException("订单"+ i + ": " +  Message.ADDRESS_ID_IS_NULL_OR_LESS_THAN_ONE);
        }
    }

    // 验证价格
    private void validatePrice(OrderAddDTO orderAddDTO,int  i){
        BigDecimal totalAmount = orderAddDTO.getTotalAmount();
        BigDecimal productSalePrice = orderTxService.getProductSalePrice(orderAddDTO.getSpecId());
        BigDecimal blackTotalPrice = productSalePrice.multiply(BigDecimal.valueOf(orderAddDTO.getProductQuantity())).setScale(2, RoundingMode.HALF_UP);
        BigDecimal frontPrice = totalAmount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal abs = blackTotalPrice.subtract(frontPrice).abs();
        BigDecimal bigDecimal = new BigDecimal("0.01");
        if(abs.compareTo(bigDecimal) > 0 )
            throw new transactionalException("订单"+ i + ": " + Message.PRICE_IS_DIFFERENCE);
    }

    //验证库存
    private void validateStock(OrderAddDTO orderAddDTO,int i,List<Integer> saleStock,List<Integer> preStock){
        Integer productSaleStock = saleStock.get(i);
        Integer productPreStock = preStock.get(i);
        if(orderAddDTO.getProductQuantity() > (productSaleStock - productPreStock))
            throw new transactionalException("订单"+ i + ": " + Message.QUANTITY_OVER_SALE_STOCK);
    }

/*    private PageResult<List<OrderRealShowVO>,Double> getOrdersBySql(Long userId, Long lastScore, Integer pageSize){
        Long orderId = lastScore% 1000000L ;
         long epochMilli = (lastScore / 1000000L)*1000;
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault());
        List<OrderShowVO> orderList = orderTxService.getOrderList(userId,localDateTime, pageSize + 1);

    }*/






}
