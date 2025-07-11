package com.zhuxi.service.Impl;

import com.zhuxi.Constant.Message;
import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.service.AdminProductService;
import com.zhuxi.service.TxService.ProductTxService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.RealStock.RealStockDTO;
import src.main.java.com.zhuxi.pojo.DTO.product.*;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminProductVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductSpecDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductSpecVO;

import java.util.List;


@Service
@Log4j2
public class AdminProductServiceImpl implements AdminProductService {
    private ProductTxService productTxService;

    public AdminProductServiceImpl(ProductTxService productTxService) {
        this.productTxService = productTxService;
    }

    /**
     * 获取所有商品状况列表
     */
    @Override
    public Result<PageResult<AdminProductVO>> getListAdminProducts(Long lastId, Integer pageSize, Integer DESC) {

        if(DESC == null)
            return Result.error(Message.PARAM_ERROR);

        PageResult<AdminProductVO> adminProductVO;
        if(DESC.equals(1))
            adminProductVO = PageDesc(lastId, pageSize);
        else
            adminProductVO = PageAsc(lastId, pageSize);


        return Result.success(Message.OPERATION_SUCCESS, adminProductVO);

    }


    /**
     * 添加商品
     */
    @Override
    @Transactional
    public Result<Void> add(ProductAddDTO productAddDTO) {
        if (productAddDTO == null || productAddDTO.getBase() == null || productAddDTO.getSpec() == null)
            return Result.error(Message.BODY_NO_MAIN_OR_IS_NULL);

        productTxService.addBase(productAddDTO.getBase());
        Long id = productAddDTO.getBase().getId();

        productTxService.addSpec(productAddDTO.getSpec(), id);


        List<RealStockDTO> realStockDTO = productAddDTO.getSpec().stream().map(productSpecDTO -> {
            RealStockDTO realStockDTO1 = new RealStockDTO();
            realStockDTO1.setProductId(id);
            realStockDTO1.setSpecId(productSpecDTO.getId());
            log.warn("sepcId : {}", productSpecDTO.getId());
            return realStockDTO1;
        }).toList();

        productTxService.addRealStock(realStockDTO);


        return Result.success(Message.OPERATION_SUCCESS);
    }

    /**
     * 删除商品
     */
    @Override
    @Transactional
    public Result<Void> delete(Long id) {
        if (id == null)
            return Result.error(Message.PRODUCT_ID_IS_NULL);

        productTxService.delete(id);

        return Result.success(Message.OPERATION_SUCCESS);
    }

    /**
     * 获取商品规格详情
     */
    @Override
    public Result<List<ProductSpecDetailVO>> getProductSpecDetail(Long productId) {
        if (productId == null)
            return Result.error(Message.PRODUCT_ID_IS_NULL);

        List<ProductSpecDetailVO> productSpecDetail = productTxService.getProductSpecDetail(productId);
        return Result.success(Message.OPERATION_SUCCESS, productSpecDetail);
    }

    /**
     * 修改商品
     */
    @Override
    @Transactional
    public Result<Void> update(ProductUpdateDTO productUpdateDTO) {
        ProductBaseUpdateDTO base = productUpdateDTO.getBase();
        if (base == null)
            return Result.error(Message.BODY_NO_MAIN_OR_IS_NULL);

        Long productId = base.getId();
        if (productId == null)
            return Result.error(Message.PRODUCT_ID_IS_NULL + "或" + Message.BODY_NO_MAIN_OR_IS_NULL);

        List<ProductSpecUpdateDTO> spec = productUpdateDTO.getSpec();
        if (spec == null)
            return Result.error(Message.BODY_NO_MAIN_OR_IS_NULL);

        for (ProductSpecUpdateDTO productSpecUpdateDTO : spec){
            if (productSpecUpdateDTO.getId() == null)
                return Result.error(Message.PRODUCT_SPEC_ID_IS_NULL);
            Integer realStock = productTxService.getRealStock(productId, productSpecUpdateDTO.getId());
            if(productSpecUpdateDTO.getStock() > realStock)
                return Result.error(Message.QUANTITY_OVER_STOCK);
        }


        productTxService.updateBase(base);
        productTxService.updateSpec(spec,productId);

        return Result.success(Message.OPERATION_SUCCESS);
    }


    /**
     * 降序分页获取所有商品状况列表
     */
    private PageResult<AdminProductVO> PageDesc(Long lastId, Integer pageSize){
        boolean hasMore = false;
        boolean first = (lastId == null || lastId <= 0);

        if (first)
            lastId = Long.MAX_VALUE;

        List<AdminProductVO> items = productTxService.getListAdminProductsDESC(lastId, pageSize + 1);

        boolean hasPrevious = !first;
        if (items.size() == pageSize + 1) {
            hasMore = true;
            items = items.subList(0, pageSize);
        }

        if (!items.isEmpty()) {
            lastId = items.get(items.size() - 1).getId();
        }


        return new PageResult<>(items, lastId, hasPrevious, hasMore);
    }

    /**
     * 升序分页获取所有商品状况列表
     */
    private PageResult<AdminProductVO> PageAsc(Long lastId, Integer pageSize){
        boolean hasMore = false;
        boolean first = (lastId == null || lastId <= 0);
        if(first)
            lastId = 0L;

        List<AdminProductVO> items = productTxService.getListAdminProductsASC(lastId, pageSize + 1);

        boolean hasPrevious = !first;
        if(items.size() == pageSize + 1){
            hasMore = true;
            items = items.subList(0, pageSize);
        }

        if(!items.isEmpty()){
            lastId = items.get(items.size() - 1).getId();
        }


        return new PageResult<>(items, lastId, hasPrevious, hasMore);
    }





}
