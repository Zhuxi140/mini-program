package com.zhuxi.service;

import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductOverviewVO;

public interface ProductService {

    // 获取商品列表
    Result<PageResult<ProductOverviewVO>> getListProducts(Long lastId, Integer pageSize);

}
