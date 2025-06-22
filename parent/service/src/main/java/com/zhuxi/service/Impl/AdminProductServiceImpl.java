package com.zhuxi.service.Impl;

import com.zhuxi.Constant.Message;
import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.mapper.ProductMapper;
import com.zhuxi.service.AdminProductService;
import org.springframework.stereotype.Service;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminProductVO;

import java.util.List;


@Service
public class AdminProductServiceImpl implements AdminProductService {

    private ProductMapper productMapper;

    public AdminProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
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
     * 降序分页获取所有商品状况列表
     */
    private PageResult<AdminProductVO> PageDesc(Long lastId, Integer pageSize){
        boolean hasMore = false;
        Long nextCursor = null;

        if (lastId == null || lastId <= 0) {
            lastId = Long.MAX_VALUE;
        }

        List<AdminProductVO> items = productMapper.getListAdminProductsDESC(lastId, pageSize + 1);

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

        List<AdminProductVO> items = productMapper.getListAdminProductsASC(lastId, pageSize + 1);

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
