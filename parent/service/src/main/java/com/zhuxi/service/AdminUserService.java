package com.zhuxi.service;

import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import src.main.java.com.zhuxi.pojo.VO.AdminUserVO;

public interface AdminUserService {


    // 获取用户信息列表(管理员端)
    Result<PageResult<AdminUserVO>> getListUser(Integer lastId, Integer pageSize);


}
