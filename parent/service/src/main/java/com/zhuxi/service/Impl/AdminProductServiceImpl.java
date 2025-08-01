package com.zhuxi.service.Impl;

import cn.hutool.core.lang.Snowflake;
import com.zhuxi.Constant.Message;
import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.service.business.AdminProductService;
import com.zhuxi.service.Tx.ProductTxService;
import com.zhuxi.utils.IdSnowFLake;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.RealStock.RealStockDTO;
import src.main.java.com.zhuxi.pojo.DTO.product.*;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminProductVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductSpecDetailVO;

import java.util.List;


@Service
@Log4j2
public class AdminProductServiceImpl implements AdminProductService {
    private ProductTxService productTxService;
    private final IdSnowFLake snowflake;
    private RabbitTemplate rabbitTemplate;


    public AdminProductServiceImpl(ProductTxService productTxService, IdSnowFLake snowflake, RabbitTemplate rabbitTemplate) {
        this.productTxService = productTxService;
        this.snowflake = snowflake;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 获取所有商品状况列表
     */
    @Override
    public Result<PageResult> getListAdminProducts(Long lastId, Integer pageSize, Integer DESC) {

        if(DESC == null)
            return Result.error(Message.PARAM_ERROR);

        PageResult<AdminProductVO,Long> adminProductVO;
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

        ProductBaseDTO base = productAddDTO.getBase();
        base.setProductNumber(snowflake.getIdInt());
        productTxService.addBase(base);
        Long id = productAddDTO.getBase().getId();

        List<ProductSpecDTO> spec = productAddDTO.getSpec();
        for (ProductSpecDTO specC : spec){
            specC.setSpecNumber(snowflake.getIdInt());
        }
        productTxService.addSpec(spec, id);

        List<RealStockDTO> realStockDTO = productAddDTO.getSpec().stream().map(productSpecDTO -> {
            RealStockDTO realStockDTO1 = new RealStockDTO();
            realStockDTO1.setProductId(id);
            realStockDTO1.setSpecId(productSpecDTO.getId());
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
     * 上架商品
     */
    @Override
    @Transactional
    public Result<Void> putOnSale(Long id) {
        if (id == null)
            return Result.error(Message.PRODUCT_ID_IS_NULL);
        productTxService.putOnSale(id);

        rabbitTemplate.convertAndSend("product.spec.exchange","new",id);
        return Result.success(Message.OPERATION_SUCCESS);
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
        List<ProductSpecUpdateDTO> spec = productUpdateDTO.getSpec();
        if (productId == null)
            return Result.error(Message.PRODUCT_ID_IS_NULL + "或" + Message.BODY_NO_MAIN_OR_IS_NULL);

        if (spec != null) {
            for (ProductSpecUpdateDTO productSpecUpdateDTO : spec) {
                if (productSpecUpdateDTO.getId() == null)
                    return Result.error(Message.PRODUCT_SPEC_ID_IS_NULL);
                if (productSpecUpdateDTO.getStock() != null){
                    Integer realStock = productTxService.getRealStock(productId, productSpecUpdateDTO.getId());
                    if (productSpecUpdateDTO.getStock() > realStock)
                        return Result.error(Message.QUANTITY_OVER_STOCK);
                }
            }
        }

        Integer status = productTxService.updateBase(base);
        if (spec != null) {
            productTxService.updateSpec(spec,productId);
        }

        if (status == 1){
            // 修改已上架商品 发送mq
            rabbitTemplate.convertAndSend("product.spec.exchange","Already",productUpdateDTO);
        }
        return Result.success(Message.OPERATION_SUCCESS);
    }


    /**
     * 降序分页获取所有商品状况列表
     */
    private PageResult<AdminProductVO,Long> PageDesc(Long lastId, Integer pageSize){
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
    private PageResult<AdminProductVO,Long> PageAsc(Long lastId, Integer pageSize){
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
