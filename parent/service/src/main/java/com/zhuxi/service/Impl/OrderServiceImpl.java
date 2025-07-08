package com.zhuxi.service.Impl;

import com.zhuxi.Constant.Message;
import com.zhuxi.Result.Result;
import com.zhuxi.service.OrderService;
import com.zhuxi.service.TxService.OrderTxService;
import com.zhuxi.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.Order.OrderAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.Order.PaymentAddDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final JwtUtils jwtUtils;
    private final OrderTxService orderTxService;

    public OrderServiceImpl(JwtUtils jwtUtils, OrderTxService orderTxService) {
        this.jwtUtils = jwtUtils;
        this.orderTxService = orderTxService;
    }

    @Override
    @Transactional
    public Result<Void> add(OrderAddDTO orderAddDTO,String token) {
        Result<Long> jwtResult = getUserId(token);

        if (jwtResult.getCode() != 200)
            return Result.error(jwtResult.getMsg());

        Long userId = jwtResult.getData();
        if (orderAddDTO == null)
            return Result.error(Message.BODY_NO_MAIN_OR_IS_NULL);

        orderAddDTO.setUserId(userId);

        if(orderAddDTO.getProductId() == null || orderAddDTO.getSpecId() == null)
            return Result.error(Message.SPEC_ID_IS_NULL + "或" + Message.PRODUCT_ID_IS_NULL);

        String oSn = generateSn(1);
        orderAddDTO.setOrderSn(oSn);

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

        if(orderAddDTO.getAddressId() == null){
            orderAddDTO.setAddressId(orderTxService.getDefaultAddressId(userId));
            orderTxService.insert(orderAddDTO);

            String pSn = generateSn(2);
            PaymentAddDTO paymentAddDTO = new PaymentAddDTO(pSn, userId, orderAddDTO.getId(), frontPrice, 0);
            orderTxService.insertPayment(paymentAddDTO);

            return Result.success(Message.OPERATION_SUCCESS);
        }

        orderTxService.insert(orderAddDTO);

        String pSn = generateSn(2);

        PaymentAddDTO paymentAddDTO = new PaymentAddDTO(pSn, userId, orderAddDTO.getId(), frontPrice, 0);
        orderTxService.insertPayment(paymentAddDTO);

        return Result.success(Message.OPERATION_SUCCESS);
    }




    private String generateSn(Integer  type){
        if(type.equals(1)){
            String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 7);
            return String.format("%s%s%s","OD",
                    java.time.LocalDate.now().toString().replace("-", ""),uuid);
        }else if (type.equals(2)){
            String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 7);
            return String.format("%s%s%s","PAY",
                    java.time.LocalDate.now().toString().replace("-", ""),
                    uuid);
        }

        return null;
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
}
