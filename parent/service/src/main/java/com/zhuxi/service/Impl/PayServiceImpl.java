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

    public PayServiceImpl(PayTxService payTxService) {
        this.payTxService = payTxService;
    }

    /**
     * 单一商品订单支付
     */
    @Override
    @Transactional
    public Result<Void> pay(PayDTO payDTO, Long userId) {
        if (payDTO.getOrderId() ==  null || payDTO.getPaidAmount() == null)
            return Result.error(Message.BODY_NO_MAIN_OR_IS_NULL);

        payDTO.setUserId(userId);
        payTxService.pay(payDTO);
        return Result.success(Message.OPERATION_SUCCESS);
    }




}
