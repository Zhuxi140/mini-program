package com.zhuxi.service.Impl;

import com.zhuxi.Constant.Message;
import com.zhuxi.Result.Result;
import com.zhuxi.service.PayService;
import com.zhuxi.service.TxService.PayTxService;
import com.zhuxi.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.Pay.PayDTO;


@Service
public class PayServiceImpl implements PayService {

    private final PayTxService payTxService;
    private final JwtUtils jwtUtils;

    public PayServiceImpl(PayTxService payTxService, JwtUtils jwtUtils) {
        this.payTxService = payTxService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * 单一商品订单支付
     */
    @Override
    @Transactional
    public Result<Void> pay(PayDTO payDTO, String token) {
        if (payDTO.getOrderId() ==  null || payDTO.getPaidAmount() == null)
            return Result.error(Message.BODY_NO_MAIN_OR_IS_NULL);

        Result<Long> result = getUserId(token);
        if (result.getCode() != 200)
            return Result.error(result.getMsg());
        payDTO.setUserId(result.getData());

        payTxService.pay(payDTO);
        return Result.success(Message.OPERATION_SUCCESS);
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
