package com.zhuxi.service.business;

import com.zhuxi.Result.Result;
import com.zhuxi.pojo.DTO.Order.OrderAddDTO;

import java.util.List;

public interface OrderService {

    // 创建单个商品订单
    Result<String> add(OrderAddDTO orderAddDTO,Long userId);

    // 创建多个商品订单(订单组)
    Result<String> addGroup(List<OrderAddDTO> orderAddDTO, Long userId);

    //取消订单
    Result<Void> cancelOrder(String orderSn);

    //取消订单组订单
    Result<Void> cancelOrderGroup(Long groupId, Long userId);

    // 获取用户订单列表
    Result getOrderList(Long userId, Long lastScore, Integer pageSize, boolean isLast);

    // 删除订单
    Result<Void> deleteOrder(String orderSn, Long userId);

    //单一订单获取状态接口(轮询）
    Result<Integer> getOrderStatus(String orderSn,Integer waitTime);

    // 订单组获取状态接口（轮询）
    Result<Integer> getOrderGroupStatus(String groupSn,Integer waitTime);

}
