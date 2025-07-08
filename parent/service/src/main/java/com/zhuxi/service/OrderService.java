package com.zhuxi.service;

import com.zhuxi.Result.Result;
import src.main.java.com.zhuxi.pojo.DTO.Order.OrderAddDTO;

public interface OrderService {

    Result<Void> add(OrderAddDTO orderAddDTO,String token);

}
