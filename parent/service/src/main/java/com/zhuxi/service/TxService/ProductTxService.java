package com.zhuxi.service.TxService;

import com.zhuxi.Constant.Message;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminProductVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductOverviewVO;

import java.util.List;


@Service
public class ProductTxService {

    private final ProductMapper productMapper;

    public ProductTxService(ProductMapper productMapper)
    {
        this.productMapper = productMapper;
    }



    @Transactional(readOnly = true,propagation = Propagation.REQUIRES_NEW)
    public List<ProductOverviewVO> getListProducts(Long lastId,Integer pageSize){
        List<ProductOverviewVO> listProducts = productMapper.getListProducts(lastId, pageSize);

        if (listProducts == null)
            throw new transactionalException(Message.SELECT_ERROR);

        return listProducts;
    }

    @Transactional(readOnly = true,propagation = Propagation.REQUIRES_NEW)
    public List<AdminProductVO> getListAdminProductsDESC(Long lastId, Integer pageSize){
        List<AdminProductVO> listAdminProductsDESC = productMapper.getListAdminProductsDESC(lastId, pageSize);
        if(listAdminProductsDESC == null)
            throw new transactionalException(Message.SELECT_ERROR);

        return listAdminProductsDESC;
    }

    @Transactional(readOnly = true,propagation = Propagation.REQUIRES_NEW)
    public List<AdminProductVO> getListAdminProductsASC(Long lastId,Integer pageSize){
        List<AdminProductVO> listAdminProductsASC = productMapper.getListAdminProductsASC(lastId, pageSize);
        if(listAdminProductsASC == null)
            throw new transactionalException(Message.SELECT_ERROR);

        return listAdminProductsASC;
    }
}
