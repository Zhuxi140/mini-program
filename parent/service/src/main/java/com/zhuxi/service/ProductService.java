package com.zhuxi.service;

import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import src.main.java.com.zhuxi.pojo.DTO.product.ProductAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.product.ProductUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductOverviewVO;

public interface ProductService {

    // 获取商品列表
    Result<PageResult<ProductOverviewVO>> getListProducts(Long lastId, Integer pageSize);

    // 获取商品详情
    Result<ProductDetailVO> getProductDetail(Long id);


}
