package com.zhuxi.service;

import com.zhuxi.Result.ProductPageResult;
import com.zhuxi.Result.Result;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductOverviewVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductSpecVO;
import java.util.List;

public interface ProductService {

    // 获取商品列表
    Result<ProductPageResult<ProductOverviewVO>> getListProducts(Long lastScore, Integer pageSize, Integer type,Long lastId,Boolean isLast);

    // 获取商品详情
    Result<ProductDetailVO> getProductDetail(Long id);

    // 获取商品规格
    Result<List<ProductSpecVO>> getProductSpec(Long productId);

}
