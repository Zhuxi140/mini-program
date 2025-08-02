package com.zhuxi.service.Tx;

import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Exception.MQException;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.BatchResult;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.RealStock.RealStockDTO;
import src.main.java.com.zhuxi.pojo.DTO.product.*;
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


    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<snowFlakeMap> getSnowFlakeMap(Long lastId,int pageSize){
        List<snowFlakeMap> snowFlakeMap = productMapper.getSnowFlakeMap(lastId, pageSize);
        if (snowFlakeMap==null ||snowFlakeMap.isEmpty())
            throw new MQException(MessageReturn.SELECT_ERROR);
        return snowFlakeMap;
    }


    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public Long getProductSnowFlakeById(Long id) {
        Long snowFlakeById = productMapper.getProductSnowFlakeById(id);
        if(snowFlakeById == null)
            throw new MQException(MessageReturn.SELECT_ERROR);
        return snowFlakeById;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public Long getSpecSnowFlakeById(Long id) {
        Long snowFlakeById = productMapper.getSpecSnowFlakeById(id);
        if(snowFlakeById == null)
            throw new MQException(MessageReturn.SELECT_ERROR);
        return snowFlakeById;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<Long> getSpecSnowFlakeByIdList(Long ProductId) {
        List<Long> snowFlakeById = productMapper.getSpecSnowFlakeByIdList(ProductId);
        if(snowFlakeById == null || snowFlakeById.isEmpty())
            throw new MQException(MessageReturn.SELECT_ERROR);
        return snowFlakeById;
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void putOnSale(Long productId){

        boolean existsErrorSpec = productMapper.isExistsErrorSpec(productId);
        if (!existsErrorSpec){
            throw new transactionalException(MessageReturn.EXISTS_ERROR_SPEC);
        }
        int i = productMapper.putOnSale(productId);
        if(i != 1)
            throw new transactionalException(MessageReturn.PUT_SALE_ERROR);
    }


    @Transactional(readOnly = true)
    public Long getProductIdBySnowflakeId(Long snowflake_id){
        Long productId = productMapper.getProductIdBySnowflakeId(snowflake_id);
        if(productId == null)
            throw new transactionalException(MessageReturn.SELECT_ERROR);
        return productId;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<PIdSnowFlake> getSaleProductId(Long lastId,int pageSize){
        List<PIdSnowFlake> saleProductId = productMapper.getSaleProductId(lastId, pageSize);
        if(saleProductId == null || saleProductId.isEmpty())
            throw new transactionalException(MessageReturn.SELECT_ERROR);
        return saleProductId;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public Long getSaleProductIdOne(Long productId){
        Long saleProductIdOne = productMapper.getSaleProductIdOne(productId);
        if(saleProductIdOne ==  null)
            throw new transactionalException(MessageReturn.SELECT_ERROR);
        return saleProductIdOne;
    }


    @Transactional(readOnly = true,propagation=Propagation.SUPPORTS)
    public List<SpecRedisDTO> getSpec(List<Long> saleProductId){
        List<SpecRedisDTO> spec = productMapper.getSpec(saleProductId);
        if(spec==null || spec.isEmpty())
            throw new transactionalException(MessageReturn.SELECT_ERROR);

        return spec;
    }


    @Transactional(readOnly = true,propagation=Propagation.SUPPORTS)
    public List<SpecRedisDTO> getSpecOne(Long productId){
        List<SpecRedisDTO> spec = productMapper.getSpecOne(productId);
        if(spec == null || spec.isEmpty())
            throw new MQException(MessageReturn.SPEC_NOT_EXIST);
        return spec;
    }



    @Transactional(readOnly = true,propagation=Propagation.SUPPORTS)
    public ProductDetailVO getProductDetail(Long productId,boolean isHit,Long ProductNumber){
        if (!isHit){
            productId = getProductIdBySnowflakeId(ProductNumber);
        }
        ProductDetailVO productDetail = productMapper.getProductDetail(productId);
        if(productDetail == null)
            throw new transactionalException(MessageReturn.SELECT_ERROR);

        return productDetail;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<Long> getAllProductId(Long lastId,int pageSize){
        return productMapper.getAllProductId(lastId,pageSize);
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<ProductSpecVO>getProductSpec(Long productId,boolean isHit,Long ProductNumber){
        if (!isHit){
            productId = getProductIdBySnowflakeId(ProductNumber);
        }
        List<ProductSpecVO> productSpec = productMapper.getProductSpec(productId);
        if(productSpec.isEmpty())
            throw new transactionalException(MessageReturn.SELECT_ERROR);

        return productSpec;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<ProductOverviewVO> getListProductsByCreate(LocalDateTime dateTime, Integer pageSize){
        List<ProductOverviewVO> listProducts = productMapper.getListProductByCreate(dateTime, pageSize);

        if (listProducts.isEmpty())
            throw new transactionalException(MessageReturn.SELECT_ERROR);

        return listProducts;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<ProductOverviewVO> getListProductByPriceDESC(Long lastId,BigDecimal price, Integer pageSize){
        List<ProductOverviewVO> listProducts = productMapper.getListProductByPriceDESC(lastId,price, pageSize);

        if (listProducts.isEmpty())
            throw new transactionalException(MessageReturn.SELECT_ERROR);

        return listProducts;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<ProductOverviewVO> getListProductByPriceASC(Long lastId,BigDecimal price, Integer pageSize){
        List<ProductOverviewVO> listProducts = productMapper.getListProductByPriceASC(lastId,price, pageSize);

        if (listProducts.isEmpty())
            throw new transactionalException(MessageReturn.ALREADY_NO_PRODUCT);

        return listProducts;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<ProductDetailVO> getListProduct(Long lastId,Integer pageSize){
        return productMapper.getListProduct(lastId, pageSize);
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public ProductDetailVO getListProductMQ(Long lastId){
        ProductDetailVO product = productMapper.getListProductMQ(lastId);
        if(product == null)
            throw new MQException(MessageReturn.PRODUCT_ID_NOT_EXIST);

        return  product;
    }

    @Transactional(readOnly = true)
    public List<AdminProductVO> getListAdminProductsDESC(Long lastId, Integer pageSize){
        List<AdminProductVO> listAdminProductsDESC = productMapper.getListAdminProductsDESC(lastId, pageSize);
        if(listAdminProductsDESC.isEmpty())
            throw new transactionalException(MessageReturn.SELECT_ERROR);

        return listAdminProductsDESC;
    }

    @Transactional(readOnly = true)
    public List<AdminProductVO> getListAdminProductsASC(Long lastId,Integer pageSize){
        List<AdminProductVO> listAdminProductsASC = productMapper.getListAdminProductsASC(lastId, pageSize);
        if(listAdminProductsASC.isEmpty())
            throw new transactionalException(MessageReturn.SELECT_ERROR);

        return listAdminProductsASC;
    }

    @Transactional(readOnly = true)
    public List<ProductSpecDetailVO> getProductSpecDetail(Long productId){
        List<ProductSpecDetailVO> productSpecDetail = productMapper.getProductSpecDetail(productId);
        if(productSpecDetail.isEmpty())
            throw new transactionalException(MessageReturn.SELECT_ERROR);

        return productSpecDetail;
    }

    @Transactional(readOnly = true)
    public Integer getRealStock(Long productId, Long specId){
        Integer realStock = productMapper.getRealStock(productId, specId);
        if(realStock < 0)
            throw new transactionalException(MessageReturn.SELECT_ERROR);

        return realStock;
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void addBasePics(String coverUrl,List<String> images,Long productId){
        if(productMapper.addBasePics(coverUrl,images,productId) < 0)
            throw new transactionalException(MessageReturn.INSERT_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void addSpecCoverUrl(String coverUrl,Long productID,Long id){
        if(productMapper.addSpecCoverUrl(coverUrl,productID,id) < 0)
            throw new transactionalException(MessageReturn.INSERT_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public Integer updateBase(ProductBaseUpdateDTO productBaseUpdateDTO){

        if(productMapper.updateProductBase(productBaseUpdateDTO) <= 0)
            throw new transactionalException(MessageReturn.UPDATE_ERROR);

        Integer productStatus = productMapper.getProductStatus(productBaseUpdateDTO.getId());
        if(productStatus == null)
            throw new transactionalException(MessageReturn.NO_PRODUCT);
        return productStatus;
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
                        throw new transactionalException(MessageReturn.UPDATE_ERROR);
            }
        }
    }

    // 添加商品基础信息
    @Transactional(rollbackFor = transactionalException.class)
    public void addBase(ProductBaseDTO productBaseDTO){
        if(!productMapper.addBase(productBaseDTO))
            throw new transactionalException(MessageReturn.INSERT_ERROR);
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
                            throw new transactionalException(MessageReturn.INSERT_ERROR);
                }
            }
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void addRealStock(List<RealStockDTO> realStockDTO){
        if(!productMapper.addRealStock(realStockDTO))
            throw new transactionalException(MessageReturn.INSERT_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void delete(Long id){
        if (productMapper.deleteProductRealStock(id) <= 0){
            throw new transactionalException(MessageReturn.DELETE_ERROR);
        }
        if (productMapper.deleteProductSpec(id) <= 0){
            throw new transactionalException(MessageReturn.DELETE_ERROR);
        }
        if(productMapper.deleteProductBase(id) <= 0)
            throw new transactionalException(MessageReturn.DELETE_ERROR);
    }
}
