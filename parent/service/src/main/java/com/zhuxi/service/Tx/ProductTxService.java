package com.zhuxi.service.Tx;

import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Exception.MQException;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.mapper.ProductMapper;
import com.zhuxi.pojo.DTO.Admin.DashboardDTO;
import com.zhuxi.pojo.VO.Product.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.BatchResult;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.zhuxi.pojo.DTO.RealStock.RealStockDTO;
import com.zhuxi.pojo.DTO.product.*;
import com.zhuxi.pojo.VO.Admin.AdminProductVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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


    @Transactional(rollbackFor = transactionalException.class)
    public void addSupplier(SupplierAddDTO supplierAddDTO){
        int i = productMapper.addSupplier(supplierAddDTO);
        if (i != 1){
            throw new transactionalException(MessageReturn.INSERT_ERROR);
        }
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void updateSupplier(Integer rating,Integer isActive,Integer id){
        int i = productMapper.updateSupplier(rating,isActive,id);
        if (i != 1){
            throw new transactionalException(MessageReturn.UPDATE_ERROR);
        }
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<snowFlakeMap> getSnowFlakeMap(Long lastId,int pageSize){
        List<snowFlakeMap> snowFlakeMap = productMapper.getSnowFlakeMap(lastId, pageSize);
        if (snowFlakeMap==null ||snowFlakeMap.isEmpty())
            throw new MQException(MessageReturn.SELECT_ERROR);
        return snowFlakeMap;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<SupplierVO> getSupplierList(Integer lastId,Integer pageSize){
        List<SupplierVO> supplierList = productMapper.getSupplierList(lastId, pageSize);
        if (supplierList == null){
            throw new MQException(MessageReturn.SELECT_ERROR);
        }
        return supplierList;
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void purchase(newProductPurchase New){
        int purchase = productMapper.purchase(New);
        if (purchase != 1){
            throw new transactionalException(MessageReturn.PURCHASE_ERROR);
        }
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void updateRealStock(Long specId, Integer quantity){
        int updateRealStock = productMapper.updateRealStock(quantity,specId);
        if (updateRealStock != 1){
            throw new transactionalException(MessageReturn.UPDATE_ERROR);
        }
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void updateSpec(BigDecimal purchasePrice, Long specId){
        int updateSpec = productMapper.updateSpec(purchasePrice, specId);
        if (updateSpec != 1){
            throw new transactionalException(MessageReturn.UPDATE_ERROR);
        }
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public void isExistsSupplier(Integer supplierId){
        int existsSupplier = productMapper.isExistSupplier(supplierId);
        if (existsSupplier == 0){
            throw new MQException(MessageReturn.SUPPLIER_NOT_EXIST);
        }
    }



    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public Long getProductSnowFlakeById(Long id) {
        Long snowFlakeById = productMapper.getProductSnowFlakeById(id);
        if(snowFlakeById == null)
            throw new MQException(MessageReturn.SELECT_ERROR);
        return snowFlakeById;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public void checkStock(Long productId){
        List<Integer> realStockList = productMapper.getRealStockList(productId);
        for (Integer realStock : realStockList){
            if (realStock > 0)
                throw new MQException(MessageReturn.PRODUCT_HAVE_STOCK);
        }
        List<Integer> specStockList = productMapper.getSpecStockList(productId);
        for (Integer specStock : specStockList){
            if (specStock > 0)
                throw new MQException(MessageReturn.PRODUCT_HAVE_STOCK);
        }
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

    @Transactional(rollbackFor = transactionalException.class)
    public void stopSale(Long productId){
        int i = productMapper.stopSale(productId);
        if(i != 1)
            throw new transactionalException(MessageReturn.STOP_SALE_ERROR);
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

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<AdminProductVO> getListAdminProductsDESC(Long lastId, Integer pageSize){
        List<AdminProductVO> listAdminProductsDESC = productMapper.getListAdminProductsDESC(lastId, pageSize);
        if(listAdminProductsDESC.isEmpty())
            throw new transactionalException(MessageReturn.SELECT_ERROR);

        return listAdminProductsDESC;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<AdminProductVO> getListAdminProductsASC(Long lastId,Integer pageSize){
        List<AdminProductVO> listAdminProductsASC = productMapper.getListAdminProductsASC(lastId, pageSize);
        if(listAdminProductsASC.isEmpty())
            throw new transactionalException(MessageReturn.SELECT_ERROR);

        return listAdminProductsASC;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<ProductSpecDetailVO> getProductSpecDetail(Long productId){
        List<ProductSpecDetailVO> productSpecDetail = productMapper.getProductSpecDetail(productId);
        if(productSpecDetail.isEmpty())
            throw new transactionalException(MessageReturn.SELECT_ERROR);

        return productSpecDetail;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
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
    public Integer updateBase(ProductBaseUpdateDTO productBaseUpdateDTO,boolean isSupplier){
        if (isSupplier){
            int exist = productMapper.isExist(productBaseUpdateDTO.getSupplierId());
            if(exist <= 0){
                throw new transactionalException(MessageReturn.SUPPLIER_ID_NOT_EXIST);
            }
        }

        if(productMapper.updateProductBase(productBaseUpdateDTO) <= 0){
            throw new transactionalException(MessageReturn.UPDATE_ERROR);
        }

        Integer productStatus = productMapper.getProductStatus(productBaseUpdateDTO.getId());
        if(productStatus == null){
            throw new transactionalException(MessageReturn.NO_PRODUCT);
        }
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
    public void addSpec(List<SpecAddDTO> SpecAddDTO){
        try {
            int i = productMapper.addSpecOnce(SpecAddDTO);
            if (i != 1) {
                throw new transactionalException(MessageReturn.INSERT_ERROR);
            }
        }catch (DuplicateKeyException  e){
            throw new transactionalException("已存在该规格");
        }

        List<RealStockDTO> realStockDTOS = new ArrayList<>();
        for (SpecAddDTO specAddDTO : SpecAddDTO){
            RealStockDTO realStockDTO = new RealStockDTO();
            realStockDTO.setProductId(specAddDTO.getProductId());
            realStockDTO.setSpecId(specAddDTO.getId());
            realStockDTOS.add(realStockDTO);
        }
        Boolean b = productMapper.addRealStock(realStockDTOS);
        if (!b){
            throw new transactionalException(MessageReturn.INSERT_ERROR);
        }
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public DashboardDTO getDashboardDTO(){
        DashboardDTO productBase = productMapper.getDashboardDTO();
        if(productBase == null){
            throw new transactionalException(MessageReturn.SELECT_ERROR);
        }
        return productBase;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<Map<String,Object>> getProfitData(Integer targetYear){
        List<Map<String, Object>> profitDate = productMapper.getProfitDate(targetYear);
        if (profitDate == null){
            throw new transactionalException(MessageReturn.NO_PROFIT_RECORD);
        }
        return profitDate;
    }





    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public void isExistsOnSale(Long productId){
        if(productMapper.getProductStatus(productId) == 1){
            throw new transactionalException("该商品还在上架状态，请先下架。");
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
