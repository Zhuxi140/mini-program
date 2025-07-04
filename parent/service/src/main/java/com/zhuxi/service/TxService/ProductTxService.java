package com.zhuxi.service.TxService;

import com.zhuxi.Constant.Message;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.product.ProductBaseDTO;
import src.main.java.com.zhuxi.pojo.DTO.product.ProductSpecDTO;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminProductVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductOverviewVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductSpecVO;

import java.util.List;


@Slf4j
@Service
public class ProductTxService {

    private final ProductMapper productMapper;

    public ProductTxService(ProductMapper productMapper)
    {
        this.productMapper = productMapper;
    }



    @Transactional(readOnly = true,propagation = Propagation.REQUIRES_NEW)
    public ProductDetailVO getProductDetail(Long id){

        ProductDetailVO productDetail = productMapper.getProductDetail(id);
        if(productDetail == null)
            throw new transactionalException(Message.SELECT_ERROR);

        return productDetail;
    }

    @Transactional(readOnly = true,propagation = Propagation.REQUIRES_NEW)
    public List<ProductSpecVO>getProductSpec(Long productId){
        List<ProductSpecVO> productSpec = productMapper.getProductSpec(productId);
        if(productSpec == null)
            throw new transactionalException(Message.SELECT_ERROR);

        return productSpec;
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

    @Transactional(rollbackFor = transactionalException.class)
    public void updateBase(ProductBaseDTO productBaseDTO){
        if(productMapper.updateProductBase(productBaseDTO) <= 0)
            throw new transactionalException(Message.UPDATE_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void updateSpec(List<ProductSpecDTO> productSpecDTO,Long id){
        if(productMapper.updateProductSpec(productSpecDTO,id) <= 0)
            throw new transactionalException(Message.UPDATE_ERROR);
    }

    // 添加商品基础信息
    @Transactional(rollbackFor = transactionalException.class)
    public void addBase(ProductBaseDTO productBaseDTO){
        if(!productMapper.addBase(productBaseDTO))
            throw new transactionalException(Message.INSERT_ERROR);
    }

    // 添加商品规格信息
    @Transactional(rollbackFor = transactionalException.class)
    public void addSpec(List<ProductSpecDTO> productSpecDTO, Long id){
        if(!productMapper.addSpec(productSpecDTO,id))
            throw new transactionalException(Message.INSERT_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void delete(Long id){
        if (productMapper.deleteProductSpec(id) <= 0){
            throw new transactionalException(Message.DELETE_ERROR);
        }
        if(productMapper.deleteProductBase(id) <= 0)
            throw new transactionalException(Message.DELETE_ERROR);
    }
}
