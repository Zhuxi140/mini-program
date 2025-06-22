package com.zhuxi.service.Impl;

import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.mapper.ProductMapper;
import com.zhuxi.service.ProductService;
import org.springframework.stereotype.Service;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductOverviewVO;

import java.util.List;


@Service
public class ProductServiceImpl implements ProductService {
    private ProductMapper productMapper;

    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }


    /**
     * 获取商品列表
     */
    @Override
    public Result<PageResult<ProductOverviewVO>> getListProducts(Long lastId, Integer pageSize) {

        boolean hasPrevious = false;
        boolean hasMore = false;

        if(lastId == null || lastId < 0)
            lastId = Long.MAX_VALUE;

        List<ProductOverviewVO> listProducts = productMapper.getListProducts(lastId, pageSize + 1);

        if(listProducts.size() == pageSize + 1){
            hasMore = true;
            listProducts = listProducts.subList(0, pageSize);
        }

        if(!listProducts.isEmpty()){
            lastId = listProducts.get(listProducts.size() - 1).getId();
        }

        return Result.success(new PageResult<>(listProducts, lastId, hasPrevious, hasMore));
    }
}
