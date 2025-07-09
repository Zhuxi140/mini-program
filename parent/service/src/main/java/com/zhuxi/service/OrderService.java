package com.zhuxi.service;

import com.zhuxi.Result.Result;
import src.main.java.com.zhuxi.pojo.DTO.Order.OrderAddDTO;

import java.util.List;

public interface OrderService {

    // 创建单个商品订单
    Result<Void> add(OrderAddDTO orderAddDTO,String token);

    // 创建多个商品订单(订单组)
    Result<Void> addGroup(List<OrderAddDTO> orderAddDTO, String token);
}
