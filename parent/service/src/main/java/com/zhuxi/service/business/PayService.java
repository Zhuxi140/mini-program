package com.zhuxi.service.business;

import com.zhuxi.Result.Result;
import src.main.java.com.zhuxi.pojo.DTO.Pay.PayDTO;

public interface PayService {

    Result<Void> pay(PayDTO payDTO,Long  token);
}
