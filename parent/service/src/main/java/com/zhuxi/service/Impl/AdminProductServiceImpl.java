package com.zhuxi.service.Impl;

import com.zhuxi.Constant.Message;
import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.mapper.ProductMapper;
import com.zhuxi.service.AdminProductService;
import com.zhuxi.service.TxService.ProductTxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.product.ProductAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.product.ProductUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminProductVO;

import java.util.List;


@Service
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
        if (productAddDTO == null || productAddDTO.getName() == null || productAddDTO.getPrice() == null)
            return Result.error(Message.BODY_NO_MAIN_OR_IS_NULL);

        productTxService.add(productAddDTO);
        return Result.success(Message.OPERATION_SUCCESS);
    }

    /**
     * 修改商品
     */
    @Override
    @Transactional
    public Result<Void> update(ProductUpdateDTO productUpdateDTO, Long id) {
        if (productUpdateDTO == null || id == null)
            return Result.error(Message.PRODUCT_ID_IS_NULL + "或" + Message.BODY_NO_MAIN_OR_IS_NULL);

        if (productUpdateDTO.getDescription() == null && productUpdateDTO.getName() == null
                && productUpdateDTO.getPrice() == null && productUpdateDTO.getStock() == null
                && productUpdateDTO.getCoverUrl() == null && productUpdateDTO.getImages() == null
                && productUpdateDTO.getOrigin() == null && productUpdateDTO.getStatus() == null
        )
            return Result.error(Message.AT_LEAST_ONE_FIELD);

        productTxService.update(productUpdateDTO,id);

        return Result.success(Message.OPERATION_SUCCESS);
    }


    /**
     * 降序分页获取所有商品状况列表
     */
    private PageResult<AdminProductVO> PageDesc(Long lastId, Integer pageSize){
        boolean hasMore = false;
        Long nextCursor = null;

        if (lastId == null || lastId <= 0) {
            lastId = Long.MAX_VALUE;
        }

        List<AdminProductVO> items = productTxService.getListAdminProductsDESC(lastId, pageSize + 1);


        if (items.size() == pageSize + 1) {
            hasMore = true;
            items = items.subList(0, pageSize);
        }

        if (!items.isEmpty()) {
            nextCursor = items.get(items.size() - 1).getId();
        }

        boolean hasPrevious = lastId != Integer.MAX_VALUE;

        return new PageResult<>(items, nextCursor, hasPrevious, hasMore);
    }

    /**
     * 升序分页获取所有商品状况列表
     */
    private PageResult<AdminProductVO> PageAsc(Long lastId, Integer pageSize){
        boolean hasMore = false;
        Long nextCursor = null;

        if(lastId == null)
            lastId = 0L;

        List<AdminProductVO> items = productTxService.getListAdminProductsASC(lastId, pageSize + 1);

        if(items.size() == pageSize + 1){
            hasMore = true;
            items = items.subList(0, pageSize);
        }

        if(!items.isEmpty()){
            nextCursor = items.get(items.size() - 1).getId();
        }

        boolean hasPrevious = lastId != 0;

        return new PageResult<>(items, nextCursor, hasPrevious, hasMore);
    }





}
