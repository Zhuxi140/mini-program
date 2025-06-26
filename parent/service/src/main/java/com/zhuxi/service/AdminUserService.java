package com.zhuxi.service;

import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminUserVO;

public interface AdminUserService {


    // 获取用户信息列表(管理员端)
    Result<PageResult<AdminUserVO>> getListUser(Long lastId, Integer pageSize,Integer DESC);

    // 禁用用户
    Result<Void> disableUser(Integer status,Long id);
}
