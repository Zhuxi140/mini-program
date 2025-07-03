package com.zhuxi.service;

import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import src.main.java.com.zhuxi.pojo.DTO.product.ProductAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.product.ProductUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminProductVO;

public interface AdminProductService {

    Result<PageResult<AdminProductVO>> getListAdminProducts(Long lastId, Integer pageSize,Integer DESC);

    Result<Void> update(ProductUpdateDTO productUpdateDTO, Long id);

    Result<Void> add(ProductAddDTO productAddDTO);

}
