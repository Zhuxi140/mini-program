package com.zhuxi.service;

import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import src.main.java.com.zhuxi.pojo.DTO.Order.OrderAddDTO;
import src.main.java.com.zhuxi.pojo.VO.Order.OrderRealShowVO;
import src.main.java.com.zhuxi.pojo.VO.Order.OrderShowVO;

import java.util.List;

public interface OrderService {

    // 创建单个商品订单
    Result<String> add(OrderAddDTO orderAddDTO,Long userId);

    // 创建多个商品订单(订单组)
    Result<Void> addGroup(List<OrderAddDTO> orderAddDTO, Long userId);

    //取消订单
    Result<Void> cancelOrder(String orderSn, Long userId);

    //取消订单组订单
    Result<Void> cancelOrderGroup(Long groupId, Long userId);

    // 获取用户订单列表
    PageResult<List<OrderRealShowVO>,Double> getOrderList(Long userId, Long lastScore, Integer pageSize,boolean isLast);

    // 删除订单
    Result<Void> deleteOrder(String orderSn, Long userId);

}
