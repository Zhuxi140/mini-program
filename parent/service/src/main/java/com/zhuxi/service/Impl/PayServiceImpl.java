package com.zhuxi.service.Impl;

import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Result.Result;
import com.zhuxi.service.Cache.PayRedisCache;
import com.zhuxi.service.business.PayService;
import com.zhuxi.service.Tx.PayTxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.Pay.PayDTO;


@Service
public class PayServiceImpl implements PayService {

    private final PayTxService payTxService;
    private final PayRedisCache payRedisCache;

    public PayServiceImpl(PayTxService payTxService, PayRedisCache payRedisCache) {
        this.payTxService = payTxService;
        this.payRedisCache = payRedisCache;
    }

    /**
     * 单一商品订单支付
     */
    @Override
    @Transactional
    public Result<Void> pay(PayDTO payDTO, Long userId) {
        if (payDTO.getOrderSn() ==  null || payDTO.getPaidAmount() == null)
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL);
        payDTO.setUserId(userId);
        payTxService.pay(payDTO);
        payRedisCache.refreshOrder(payDTO.getOrderSn());
        return Result.success(MessageReturn.OPERATION_SUCCESS);
    }




}
