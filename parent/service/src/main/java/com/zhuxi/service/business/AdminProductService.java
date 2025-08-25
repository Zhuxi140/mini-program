package com.zhuxi.service.business;

import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.pojo.DTO.Admin.DashboardDTO;
import com.zhuxi.pojo.DTO.product.*;
import com.zhuxi.pojo.VO.Product.ProductSpecDetailVO;
import com.zhuxi.pojo.VO.Product.ProductSpecIdVO;
import com.zhuxi.pojo.VO.Product.SupplierVO;

import java.util.List;
import java.util.Map;

public interface AdminProductService {

    Result<PageResult> getListAdminProducts(Long lastId, Integer pageSize, Integer DESC);

    Result<Void> update(ProductUpdateDTO productUpdateDTO);

    Result<ProductSpecIdVO> add(ProductAddDTO productAddDTO);

    Result<Void> delete(Long id);

    Result<List<ProductSpecDetailVO> > getProductSpecDetail(Long productId);

    //上架
    Result<Void> putOnSale(Long id);

    Result<Void> stopSale(Long id);

    // 进货
    Result<Void> purchase(newProductPurchase newProductPurchase);

    //展示供应商列表
    Result<PageResult<SupplierVO, Integer>> getSupplierList(Integer lastId, Integer pageSize);

    //添加商品规格
    Result<Void> addSpec(List<SpecAddDTO> SpecAddDTO);

    //看板数据
    Result<DashboardDTO> getDashboardData();

    // 可视化利润和用户增长
    Result<List<Map<String, Object>>> getProfitData(Integer targetYear);


    // 增加供应商
    Result<Void> addSupplier(SupplierAddDTO supplierAddDTO);

    // 修改供应商信息
    Result<Void> updateSupplier(Integer rating,Integer isActive,Integer id);


}
