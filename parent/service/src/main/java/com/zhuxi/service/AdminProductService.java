package com.zhuxi.service;

import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminProductVO;

public interface AdminProductService {

    Result<PageResult<AdminProductVO>> getListAdminProducts(Long lastId, Integer pageSize,Integer DESC);

}
