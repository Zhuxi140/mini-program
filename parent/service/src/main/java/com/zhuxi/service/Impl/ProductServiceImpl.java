package com.zhuxi.service.Impl;

import com.zhuxi.Constant.Message;
import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.mapper.ProductMapper;
import com.zhuxi.service.ProductService;
import com.zhuxi.service.TxService.ProductTxService;
import org.springframework.stereotype.Service;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductOverviewVO;

import java.util.List;


@Service
public class ProductServiceImpl implements ProductService {

    private final ProductTxService productTxService;

    public ProductServiceImpl(ProductTxService productTxService) {

        this.productTxService = productTxService;
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

        List<ProductOverviewVO> listProducts = productTxService.getListProducts(lastId, pageSize + 1);

        if(listProducts.size() == pageSize + 1){
            hasMore = true;
            listProducts = listProducts.subList(0, pageSize);
        }

        if(!listProducts.isEmpty()){
            lastId = listProducts.get(listProducts.size() - 1).getId();
        }

        return Result.success(new PageResult<>(listProducts, lastId, hasPrevious, hasMore));
    }

    /**
     * 获取商品详情
     */
    @Override
    public Result<ProductDetailVO> getProductDetail(Long id) {

        if(id == null)
            return Result.error(Message.ARTICLE_ID_IS_NULL);

        ProductDetailVO productDetail = productTxService.getProductDetail(id);
        return Result.success(Message.OPERATION_SUCCESS, productDetail);
    }
}
