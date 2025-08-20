package com.zhuxi.service.business;

import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;

import java.util.List;
import java.util.Map;

public interface AdminUserService {


    // 获取用户信息列表(管理员端)
    Result<PageResult> getListUser(Long lastId, Integer pageSize, Integer DESC);

    // 禁用用户
    Result<Void> disableUser(Integer status,Long id);

    // 获取用户近x天订单信息
    Result<PageResult> getUserOrder(Long userId,Long lastId, Integer pageSize, Integer days);


    // 获取用户增长趋势图
    Result<List<Map<String, Integer>>> getUserTrend(Integer targetYear);
}
