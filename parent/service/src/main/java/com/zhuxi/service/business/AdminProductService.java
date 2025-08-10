package com.zhuxi.service.business;

import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import src.main.java.com.zhuxi.pojo.DTO.product.ProductAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.product.ProductUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductSpecDetailVO;

import java.util.List;

public interface AdminProductService {

    Result<PageResult> getListAdminProducts(Long lastId, Integer pageSize, Integer DESC);

    Result<Void> update(ProductUpdateDTO productUpdateDTO);

    Result<Void> add(ProductAddDTO productAddDTO);

    Result<Void> delete(Long id);

    Result<List<ProductSpecDetailVO> > getProductSpecDetail(Long productId);

    //上架
    Result<Void> putOnSale(Long id);

    Result<Void> stopSale(Long id);


}
