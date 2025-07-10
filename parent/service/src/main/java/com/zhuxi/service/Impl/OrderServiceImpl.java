package com.zhuxi.service.Impl;

import com.zhuxi.Constant.Message;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.Result.Result;
import com.zhuxi.service.OrderService;
import com.zhuxi.service.TxService.OrderTxService;
import com.zhuxi.utils.IdSnowFLake;
import com.zhuxi.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.Order.InventoryLockAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.Order.OrderAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.Order.OrderGroupDTO;
import src.main.java.com.zhuxi.pojo.DTO.Order.PaymentAddDTO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    private final JwtUtils jwtUtils;
    private final OrderTxService orderTxService;
    private final IdSnowFLake idSnowFLake;

    public OrderServiceImpl(JwtUtils jwtUtils, OrderTxService orderTxService, IdSnowFLake idSnowFLake) {
        this.jwtUtils = jwtUtils;
        this.orderTxService = orderTxService;
        this.idSnowFLake = idSnowFLake;
    }

    /**
     * 添加订单
     */
    @Override
    @Transactional
    public Result<Void> add(OrderAddDTO orderAddDTO,String token) {

        // 基础效验
        Result<Long> jwtResult = getUserId(token);

        if (jwtResult.getCode() != 200)
            return Result.error(jwtResult.getMsg());

        Long userId = jwtResult.getData();
        if (orderAddDTO == null)
            return Result.error(Message.BODY_NO_MAIN_OR_IS_NULL);

        orderAddDTO.setUserId(userId);

        Long productId = orderAddDTO.getProductId();
        Long specId = orderAddDTO.getSpecId();

        if(productId == null || specId == null)
            return Result.error(Message.SPEC_ID_IS_NULL + "或" + Message.PRODUCT_ID_IS_NULL);

        // 验证数量
        Integer productQuantity = orderAddDTO.getProductQuantity();
        if(productQuantity == null || productQuantity < 1)
            return Result.error(Message.QUANTITY_IS_NULL_OR_LESS_THAN_ONE);

        // 验证够购买数量 是否超出可售库存
        Integer productSaleStock = orderTxService.getProductSaleStock(specId);
        Integer productPreStock = orderTxService.getProductPreStock(specId);
        if(productQuantity > (productSaleStock - productPreStock))
            return Result.error(Message.QUANTITY_OVER_SALE_STOCK);

        //  生成订单编号
        orderAddDTO.setOrderSn(generateSn(1));

        // 验证价格
        BigDecimal totalAmount = orderAddDTO.getTotalAmount();
        if(totalAmount == null)
            return Result.error(Message.PRICE_IS_NULL);
        BigDecimal productSalePrice = orderTxService.getProductSalePrice(orderAddDTO.getSpecId());
        BigDecimal blackTotalPrice = productSalePrice.multiply(BigDecimal.valueOf(orderAddDTO.getProductQuantity())).setScale(2, RoundingMode.HALF_UP);
        BigDecimal frontPrice = totalAmount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal abs = blackTotalPrice.subtract(frontPrice).abs();
        BigDecimal bigDecimal = new BigDecimal("0.01");
        if(abs.compareTo(bigDecimal) > 0 )
            return Result.error(Message.PRICE_IS_DIFFERENCE);

        // 验证地址
        dealAddressId(orderAddDTO,userId,0);

        // 创建订单
        orderTxService.insert(orderAddDTO);
        Long id = orderAddDTO.getId();  // 获取mybatis返回的订单id

        // 创建支付编号
        String pSn = generateSn(2);
        PaymentAddDTO paymentAddDTO = new PaymentAddDTO(pSn, userId,id , frontPrice, 0);

        orderTxService.insertPayment(paymentAddDTO);
        orderTxService.insertInventoryLock(productId,specId,id, productQuantity);
        orderTxService.reduceProductSaleStock(specId,productQuantity);

        return Result.success(Message.OPERATION_SUCCESS);
    }

    /**
     * 批量添加订单
     */
    @Override
    @Transactional(rollbackFor = {transactionalException.class,RuntimeException.class })
    public Result<Void> addGroup(List<OrderAddDTO> orderAddDTO, String token) {
        if(orderAddDTO == null || orderAddDTO.isEmpty())
            return Result.error(Message.BODY_NO_MAIN_OR_IS_NULL);

        // 解析userId
        Result<Long> jwtResult = getUserId(token);
        if (jwtResult.getCode() != 200)
            return Result.error(jwtResult.getMsg());
        Long userId = jwtResult.getData();

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
    public Result<Void> cancelOrder(Long orderId, String token) {
        if (orderId == null)
            return Result.error(Message.ORDER_ID_IS_NULL);

        Result<Long> userId = getUserId(token);
        if (userId.getCode() != 200)
            return Result.error(userId.getMsg());

        orderTxService.concealOrder(orderId);
        orderTxService.releaseLockStock(orderId);

        return Result.success(Message.OPERATION_SUCCESS);
    }

    /**
     * 取消订单组
     */
    @Override
    @Transactional
    public Result<Void> cancelOrderGroup(Long groupId, String token) {
        if (groupId == null)
            return Result.error(Message.GROUP_ID_IS_NULL);
        Result<Long> userId = getUserId(token);
        if (userId.getCode() != 200)
            return Result.error(userId.getMsg());
        List<Long> orderIdList = orderTxService.getOrderIdList(groupId);
        orderTxService.concealOrderList(orderIdList);
        orderTxService.releaseLockStockList(orderIdList);

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

        }

        throw new RuntimeException(Message.ID_GENERATE_ERROR);
    }

    private Result<Long> getUserId(String token){
        if (token == null) {
            return Result.error(Message.JWT_IS_NULL);
        }

        Claims claims = jwtUtils.parseToken(token);
        if (claims == null) {
            return Result.error(Message.JWT_ERROR);
        }

        Long userId = claims.get("id", Long.class);
        if (userId == null) {
            return Result.error(Message.JWT_DATA_ERROR);
        }

        return Result.success(userId);
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


}
