package com.zhuxi.service.TxService;

import com.zhuxi.Constant.Message;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.BatchResult;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.RealStock.RealStockDTO;
import src.main.java.com.zhuxi.pojo.DTO.product.ProductBaseDTO;
import src.main.java.com.zhuxi.pojo.DTO.product.ProductBaseUpdateDTO;
import src.main.java.com.zhuxi.pojo.DTO.product.ProductSpecDTO;
import src.main.java.com.zhuxi.pojo.DTO.product.ProductSpecUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminProductVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductOverviewVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductSpecDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductSpecVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
public class ProductTxService {

    private final ProductMapper productMapper;
    private final SqlSessionTemplate sqlSessionTemplate;

    public ProductTxService(ProductMapper productMapper, SqlSessionTemplate sqlSessionTemplate)
    {
        this.productMapper = productMapper;
        this.sqlSessionTemplate = sqlSessionTemplate;
    }



    @Transactional(readOnly = true)
    public ProductDetailVO getProductDetail(Long id){

        ProductDetailVO productDetail = productMapper.getProductDetail(id);
        if(productDetail == null)
            throw new transactionalException(Message.SELECT_ERROR);

        return productDetail;
    }

    @Transactional(readOnly = true)
    public List<ProductSpecVO>getProductSpec(Long productId){
        List<ProductSpecVO> productSpec = productMapper.getProductSpec(productId);
        if(productSpec == null)
            throw new transactionalException(Message.SELECT_ERROR);

        return productSpec;
    }

    @Transactional(readOnly = true)
    public List<ProductOverviewVO> getListProductsByCreate(Long lastId,LocalDateTime dateTime, Integer pageSize){
        List<ProductOverviewVO> listProducts = productMapper.getListProductByCreate(dateTime, pageSize,lastId);

        if (listProducts == null)
            throw new transactionalException(Message.SELECT_ERROR);

        return listProducts;
    }

    @Transactional(readOnly = true)
    public List<ProductOverviewVO> getListProductByPriceDESC(Long lastId,BigDecimal price, Integer pageSize){
        List<ProductOverviewVO> listProducts = productMapper.getListProductByPriceDESC(price, pageSize,lastId);

        if (listProducts == null)
            throw new transactionalException(Message.SELECT_ERROR);

        return listProducts;
    }

    @Transactional(readOnly = true)
    public List<ProductOverviewVO> getListProductByPriceASC(Long lastId,BigDecimal price, Integer pageSize){
        List<ProductOverviewVO> listProducts = productMapper.getListProductByPriceASC(price, pageSize,lastId);

        if (listProducts == null)
            throw new transactionalException(Message.SELECT_ERROR);

        return listProducts;
    }

    @Transactional(readOnly = true)
    public List<ProductOverviewVO> getListProduct(Long lastId,Integer pageSize){
        return productMapper.getListProduct(lastId, pageSize);
    }

    @Transactional(readOnly = true)
    public List<AdminProductVO> getListAdminProductsDESC(Long lastId, Integer pageSize){
        List<AdminProductVO> listAdminProductsDESC = productMapper.getListAdminProductsDESC(lastId, pageSize);
        if(listAdminProductsDESC == null)
            throw new transactionalException(Message.SELECT_ERROR);

        return listAdminProductsDESC;
    }

    @Transactional(readOnly = true)
    public List<AdminProductVO> getListAdminProductsASC(Long lastId,Integer pageSize){
        List<AdminProductVO> listAdminProductsASC = productMapper.getListAdminProductsASC(lastId, pageSize);
        if(listAdminProductsASC == null)
            throw new transactionalException(Message.SELECT_ERROR);

        return listAdminProductsASC;
    }

    @Transactional(readOnly = true)
    public List<ProductSpecDetailVO> getProductSpecDetail(Long productId){
        List<ProductSpecDetailVO> productSpecDetail = productMapper.getProductSpecDetail(productId);
        if(productSpecDetail == null)
            throw new transactionalException(Message.SELECT_ERROR);

        return productSpecDetail;
    }

    @Transactional(readOnly = true)
    public Integer getRealStock(Long productId, Long specId){
        Integer realStock = productMapper.getRealStock(productId, specId);
        if(realStock < 0)
            throw new transactionalException(Message.SELECT_ERROR);

        return realStock;
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void addBasePics(String coverUrl,List<String> images,Long productId){
        if(productMapper.addBasePics(coverUrl,images,productId) < 0)
            throw new transactionalException(Message.INSERT_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void addSpecCoverUrl(String coverUrl,Long productID,Long id){
        if(productMapper.addSpecCoverUrl(coverUrl,productID,id) < 0)
            throw new transactionalException(Message.INSERT_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void updateBase(ProductBaseUpdateDTO productBaseUpdateDTO){
        if(productMapper.updateProductBase(productBaseUpdateDTO) <= 0)
            throw new transactionalException(Message.UPDATE_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void updateSpec(List<ProductSpecUpdateDTO> productSpecUpdateDTO, Long id){
        ProductMapper mapper = sqlSessionTemplate.getMapper(ProductMapper.class);
        for (ProductSpecUpdateDTO productSpecUpdateDTO1 : productSpecUpdateDTO) {
            mapper.updateProductSpec(productSpecUpdateDTO1,id);
        }

        List<BatchResult> batchResults = sqlSessionTemplate.flushStatements();
        for (BatchResult batchResult : batchResults) {
            if(batchResult.getUpdateCounts() != null){
                for (int updateCount : batchResult.getUpdateCounts())
                    if(updateCount <= 0)
                        throw new transactionalException(Message.UPDATE_ERROR);
            }
        }
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
            ProductMapper Mapper = sqlSessionTemplate.getMapper(ProductMapper.class);
            for (ProductSpecDTO productSpecDTO1 : productSpecDTO) {
                Mapper.addSpec(productSpecDTO1,id);
            }

        List<BatchResult> batchResults = sqlSessionTemplate.flushStatements();
            for (BatchResult batchResult : batchResults) {
                if(batchResult.getUpdateCounts() != null){
                    for (int updateCount : batchResult.getUpdateCounts())
                        if(updateCount <= 0)
                            throw new transactionalException(Message.INSERT_ERROR);
                }
            }
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void addRealStock(List<RealStockDTO> realStockDTO){
        if(!productMapper.addRealStock(realStockDTO))
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
