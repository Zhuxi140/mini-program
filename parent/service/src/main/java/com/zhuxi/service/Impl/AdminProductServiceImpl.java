package com.zhuxi.service.Impl;

import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.pojo.DTO.Admin.DashboardDTO;
import com.zhuxi.pojo.VO.Product.SupplierVO;
import com.zhuxi.service.business.AdminProductService;
import com.zhuxi.service.Tx.ProductTxService;
import com.zhuxi.utils.IdSnowFLake;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.zhuxi.pojo.DTO.RealStock.RealStockDTO;
import com.zhuxi.pojo.DTO.product.*;
import com.zhuxi.pojo.VO.Admin.AdminProductVO;
import com.zhuxi.pojo.VO.Product.ProductSpecDetailVO;
import java.math.BigDecimal;
import java.util.*;


@Service
@Log4j2
public class AdminProductServiceImpl implements AdminProductService {
    private final ProductTxService productTxService;
    private final IdSnowFLake snowflake;
    private final RabbitTemplate rabbitTemplate;
    @Value("${stock-threshold}")
    private Integer safeStock;

    public AdminProductServiceImpl(ProductTxService productTxService, IdSnowFLake snowflake,
                                   @Qualifier("rabbitTemplate")
                                   RabbitTemplate rabbitTemplate) {
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
            return Result.error(MessageReturn.PARAM_ERROR);

        PageResult<AdminProductVO,Long> adminProductVO;
        if(DESC.equals(1))
            adminProductVO = PageDesc(lastId, pageSize);
        else
            adminProductVO = PageAsc(lastId, pageSize);


        return Result.success(MessageReturn.OPERATION_SUCCESS, adminProductVO);

    }


    /**
     * 添加商品
     */
    @Override
    @Transactional
    public Result<Void> add(ProductAddDTO productAddDTO) {
        if (productAddDTO == null || productAddDTO.getBase() == null || productAddDTO.getSpec() == null)
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL);

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

        return Result.success(MessageReturn.OPERATION_SUCCESS);
    }


    /**
     * 删除商品
     */
    @Override
    @Transactional
    public Result<Void> delete(Long id) {
        if (id == null)
            return Result.error(MessageReturn.PRODUCT_ID_IS_NULL);

        // 检查是否存在库存余量
        productTxService.checkStock( id);

        PSsnowFlake pSsnowFlake = new PSsnowFlake();
        pSsnowFlake.setProductSnowflake(productTxService.getProductSnowFlakeById(id));
        pSsnowFlake.setSpecSnowflake(productTxService.getSpecSnowFlakeByIdList(id));
        productTxService.delete(id);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == TransactionSynchronization.STATUS_COMMITTED) {
                    rabbitTemplate.convertAndSend("product.spec.exchange","delete",pSsnowFlake,message -> {
                        MessageProperties props = message.getMessageProperties();
                        props.setMessageId(UUID.randomUUID().toString());
                        props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        return message;
                    });
                }else{
                    log.warn("事务未提交，截断发送消息---{删除商品:{}}", id);
                }
            }
        });
        return Result.success(MessageReturn.OPERATION_SUCCESS);
    }

    /**
     * 获取商品规格详情
     */
    @Override
    public Result<List<ProductSpecDetailVO>> getProductSpecDetail(Long productId) {
        if (productId == null)
            return Result.error(MessageReturn.PRODUCT_ID_IS_NULL);

        List<ProductSpecDetailVO> productSpecDetail = productTxService.getProductSpecDetail(productId);
        return Result.success(MessageReturn.OPERATION_SUCCESS, productSpecDetail);
    }

    /**
     * 上架商品
     */
    @Override
    @Transactional
    public Result<Void> putOnSale(Long id) {
        if (id == null)
            return Result.error(MessageReturn.PRODUCT_ID_IS_NULL);
        productTxService.putOnSale(id);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == TransactionSynchronization.STATUS_COMMITTED) {
                    rabbitTemplate.convertAndSend("product.spec.exchange", "new", id, message -> {
                        MessageProperties props = message.getMessageProperties();
                        props.setMessageId(UUID.randomUUID().toString());
                        props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        return message;
                    });
                }else{
                    log.warn("事务未提交，截断发送消息---{上架商品:{}}", id);
                }
            }
        });

        return Result.success(MessageReturn.OPERATION_SUCCESS);
    }

    /**
     * 下架商品
     */
    @Override
    @Transactional
    public Result<Void> stopSale(Long id) {
        if (id == null)
            return Result.error(MessageReturn.PRODUCT_ID_IS_NULL);
        productTxService.stopSale(id);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == TransactionSynchronization.STATUS_COMMITTED) {
                    rabbitTemplate.convertAndSend("product.spec.exchange", "stopSale", id, message -> {
                        MessageProperties props = message.getMessageProperties();
                        props.setMessageId(UUID.randomUUID().toString());
                        props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        return message;
                    });
                }else{
                    log.warn("事务未提交，截断发送消息---{下架商品:{}}", id);
                }
            }
        });


        return Result.success(MessageReturn.OPERATION_SUCCESS);
    }

    /**
     * 采购商品
     */
    @Override
    @Transactional
    public Result<Void> purchase(newProductPurchase New) {
        if(New == null){
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL);
        }
        Long specId = New.getSpecId();
        Integer supplierId = New.getSupplierId();
        Integer quantity = New.getQuantity();
        if (/*New.getProductId() ==  null ||*/  specId == null ||  supplierId == null || New.getPurchasePrice() == null || quantity == null){
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL);
        }
        //计算总额
        New.setTotalAmount(New.getPurchasePrice().multiply(new BigDecimal(New.getQuantity())));
        productTxService.isExistsSupplier(supplierId);
        productTxService.purchase(New);
        productTxService.updateRealStock(specId,quantity);
        productTxService.updateSpec(New.getPurchasePrice(),specId);
        return Result.success(MessageReturn.OPERATION_SUCCESS);
    }

    /**
     * 获取供应商列表
     */
    @Override
    public Result<PageResult<SupplierVO, Integer>> getSupplierList(Integer lastId, Integer pageSize) {
        boolean hasMore = false;
        boolean first = (lastId == null || lastId <= 0);
        boolean hasPrevious = !first;

        if ( first){
            lastId = Integer.MAX_VALUE;
        }
        List<SupplierVO> items = productTxService.getSupplierList(lastId, pageSize + 1);
        if (items.size() == pageSize + 1){
            hasMore = true;
            lastId = items.get(pageSize).getId();
            items = items.subList(0, pageSize);
        }

        PageResult<SupplierVO, Integer> supplierVOIntegerPageResult = new PageResult<>(items, lastId, hasPrevious, hasMore);

        return Result.success(MessageReturn.OPERATION_SUCCESS, supplierVOIntegerPageResult);
    }

    /**
     * 添加商品规格(仅限下架时才可添加)
     */
    @Override
    @Transactional
    public Result<Void> addSpec(List<SpecAddDTO> SpecAddDTO) {

        Result result = checkIsNull(SpecAddDTO);
        int code = result.getCode();
        if (code == 500){
            return result;
        }
        for (SpecAddDTO specAddDTO : SpecAddDTO){
            specAddDTO.setSnowflakeId(snowflake.getIdInt());
        }
        productTxService.isExistsOnSale(SpecAddDTO.get(0).getProductId());
        productTxService.addSpec(SpecAddDTO);
        return Result.success(MessageReturn.OPERATION_SUCCESS);
    }

    /**
     * 获取仪表盘数据
     */
    @Override
    public Result<DashboardDTO> getDashboardData() {
        DashboardDTO dashboardDTO = productTxService.getDashboardDTO();
        return Result.success(MessageReturn.OPERATION_SUCCESS, dashboardDTO);
    }

    /**
     * 获取利润数据
     */
    @Override
    public Result<List<Map<String, Object>>> getProfitData(Integer targetYear) {
        // 如果 是非4位数 直接返回错误
        if (targetYear != null && targetYear.toString().length() != 4){
            return Result.error(MessageReturn.YEAR_IS_NOT_4_DIGIT);
        }

        List<Map<String, Object>> profitData = productTxService.getProfitData(targetYear);
        return Result.success(MessageReturn.OPERATION_SUCCESS, profitData);
    }

    @Override
    @Transactional
    public Result<Void> addSupplier(SupplierAddDTO sa) {

        if (sa == null){
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL);
        }
        String name = sa.getName();
        String contact = sa.getContact();
        String address = sa.getAddress();
        String phone = sa.getPhone();
        if (name == null || contact == null || address == null || phone == null ||
            name.isEmpty() || contact.isEmpty() || address.isEmpty() || phone.isEmpty()){
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL);
        }

        productTxService.addSupplier(sa);

        return Result.success(MessageReturn.OPERATION_SUCCESS);
    }

    @Override
    @Transactional
    public Result<Void> updateSupplier(Integer rating, Integer isActive, Integer id) {
        if (rating == null || isActive == null || id == null){
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL);
        }

        productTxService.updateSupplier(rating, isActive, id);

        return Result.success(MessageReturn.OPERATION_SUCCESS);
    }

    /**
     * 修改商品
     */
    @Override
    @Transactional
    public Result<Void> update(ProductUpdateDTO productUpdateDTO) {

        ProductBaseUpdateDTO base = productUpdateDTO.getBase();
        List<ProductSpecUpdateDTO> spec = productUpdateDTO.getSpec();
        if (base == null && spec ==  null){
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL);
        }

        Long productId = null;
        if (base != null) {
            productId = base.getId();
        }

        if (productId == null)
            return Result.error(MessageReturn.PRODUCT_ID_IS_NULL + "或" + MessageReturn.BODY_NO_MAIN_OR_IS_NULL);

        List<Long> specIds = new ArrayList<>();
        if (spec != null) {
            for (ProductSpecUpdateDTO productSpecUpdateDTO : spec) {
                Long specId = productSpecUpdateDTO.getId();
                specIds.add(specId);
                if (specId == null)
                    return Result.error(MessageReturn.PRODUCT_SPEC_ID_IS_NULL);
                if (productSpecUpdateDTO.getStock() != null){
                    Integer realStock = productTxService.getRealStock(productId, productSpecUpdateDTO.getId());
                    if (realStock - productSpecUpdateDTO.getStock() < safeStock){
                        return Result.error(MessageReturn.QUANTITY_OVER_STOCK);
                    }
                }
            }
        }
        Integer status = productTxService.updateBase(base);
        if (spec != null) {
            productTxService.updateSpec(spec,productId);
        }

        if (status == 1){
            Long finalProductId = productId;
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status == TransactionSynchronization.STATUS_COMMITTED) {
                        MessagePostProcessor headerProcessor = message -> {
                            MessageProperties props = message.getMessageProperties();
                            props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                            props.setMessageId(UUID.randomUUID().toString());
                            Map<String, Object> headers = new HashMap<>();
                            headers.put("spec-is-null", spec == null || spec.isEmpty());
                            return MessageBuilder.fromMessage(message)
                                    .copyHeaders(headers)
                                    .build();
                        };
                        rabbitTemplate.convertAndSend("product.spec.exchange", "Already", productUpdateDTO, headerProcessor);
                    }else{
                        log.warn("事务未提交，截断发送消息---{修改商品---product:{}--spec:{}}", finalProductId, specIds);
                    }
                }
            });
        }

        return Result.success(MessageReturn.OPERATION_SUCCESS);
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


    private Result checkIsNull(List<SpecAddDTO> specAddDTO){
        if (specAddDTO == null)
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL);

        for (SpecAddDTO specAddDTO1 : specAddDTO){
            if (specAddDTO1.getProductId() == null || specAddDTO1.getSpec() == null){
                return Result.error(MessageReturn.PRODUCT_ID_IS_NULL);
            }
        }
        return Result.success();
    }


}
