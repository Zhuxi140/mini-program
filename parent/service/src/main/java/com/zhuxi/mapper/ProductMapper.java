package com.zhuxi.mapper;


import com.zhuxi.pojo.DTO.Admin.DashboardDTO;
import com.zhuxi.pojo.VO.Product.*;
import org.apache.ibatis.annotations.*;
import com.zhuxi.pojo.DTO.RealStock.RealStockDTO;
import com.zhuxi.pojo.DTO.product.*;
import com.zhuxi.pojo.VO.Admin.AdminProductVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper {

    List<ProductOverviewVO> getListProductByCreate(LocalDateTime dateTime, Integer pageSize);

    List<ProductOverviewVO> getListProductByPriceDESC(Long lastId,BigDecimal price, Integer pageSize);

    List<ProductOverviewVO> getListProductByPriceASC(Long lastId,BigDecimal price, Integer pageSize);

    List<ProductDetailVO> getListProduct(Long lastId, Integer pageSize);

    List<AdminProductVO>  getListAdminProductsDESC(Long lastId , Integer pageSize);

    List<AdminProductVO>  getListAdminProductsASC(Long lastId , Integer pageSize);

    @Select("""
    SELECT id,snowflake_id
    FROM product
    WHERE id > #{lastId} AND status = 1
    ORDER BY id ASC
    LIMIT #{pageSize}
    """)
    List<PIdSnowFlake> getSaleProductId(Long lastId,int pageSize);

    @Select("""
    SELECT snowflake_id
    FROM product
    WHERE id = #{productId} AND status = 1
    """)
    Long getSaleProductIdOne(Long productId);




    List<SpecRedisDTO> getSpec(List<Long> productIds);


    @Select("""
    SELECT product.name,
           mini_price.price,
           product.cover_url,
           product.images,
           product.description,
          product.status,
           product.origin
           FROM product JOIN (
           SELECT product_Id,MIN(price) AS price FROM spec GROUP BY product_Id
            ) mini_price
           ON mini_price.product_Id = product.id
                      WHERE product.id = #{id};
    """)
    ProductDetailVO getProductDetail(Long id);

    @Select("SELECT id AS specId,spec,price,cover_url,stock FROM spec WHERE product_id = #{id}")
    List<ProductSpecVO> getProductSpec(Long id);

    @Select("SELECT id FROM product WHERE snowflake_id = #{snowflake_id}")
    Long getProductIdBySnowflakeId(Long snowflake_id);

    @Select("""
    SELECT spec.id AS specId,
           spec.spec,
           spec.price AS sale_price,
           spec.purchase_price,
           spec.stock,
           real_stock.stock AS real_stock
    FROM spec JOIN real_stock ON real_stock.spec_id = spec.id
    WHERE spec.product_id = #{productId}
    """)
    List<ProductSpecDetailVO> getProductSpecDetail(Long productId);

    // 添加商品基础信息
    @Insert("""
    INSERT INTO product(name,description,origin,status)
    VALUES (#{name},#{description},#{origin},#{status})
    """)
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    Boolean addBase(ProductBaseDTO productBaseDTO);

    //添加规格信息
    @Options(useGeneratedKeys = true,keyProperty = "pSDto.id",keyColumn = "id")
    @Insert("""
    INSERT INTO spec(product_id,spec,stock)
    VALUE(#{productId},#{pSDto.spec},0)
    """)
    int addSpec(@Param("pSDto") ProductSpecDTO productSpecDTO,@Param("productId")  Long id);


    int addSpecOnce(List<SpecAddDTO> specAddDTO);

    //初始化真实库存记录
    Boolean addRealStock(@Param("list") List<RealStockDTO> realStockDTO);


    // 修改商品基础信息
    int updateProductBase(@Param("base") ProductBaseUpdateDTO productBaseUpdateDTO);

    @Select("SELECT COUNT(*) FROM supplier WHERE id = #{id}")
    int isExistSupplier(Integer supplierId);

    @Select("SELECT product.status FROM product WHERE id = #{id}")
    Integer getProductStatus(Long id);

    @Select("SELECT rs.stock  FROM real_stock AS rs WHERE rs.product_id = #{productId} AND rs.spec_id = #{specId}")
    Integer getRealStock(@Param("productId") Long productId, @Param("specId") Long specId);

    // 修改商品规格信息
    int updateProductSpec(@Param("specU") ProductSpecUpdateDTO productSpecUpdateDTO, @Param("productId")  Long id);

    // 删除商品基础信息
    @Delete("DELETE FROM product WHERE id = #{id}")
    int deleteProductBase(Long id);

    // 删除商品规格信息
    @Delete("DELETE FROM spec WHERE product_id = #{id}")
    int deleteProductSpec(Long id);

    @Delete("DELETE FROM real_stock WHERE product_id = #{productId}")
    int deleteProductRealStock(Long productId);

    // 添加规格图
    @Insert("""
    UPDATE spec SET cover_url = #{coverUrl} WHERE product_id = #{productID} AND id = #{id}
    """)
    int addSpecCoverUrl(String coverUrl, Long productID, Long id);

    //添加商品 封面图、详细图
    int addBasePics(String coverUrl,@Param("image") List<String> images, Long productId);



    @Select("SELECT snowflake_id FROM product WHERE id > #{lastId} ORDER BY id LIMIT #{pageSize}")
    List<Long> getAllProductId(Long lastId,int pageSize);


    @Update("UPDATE product SET status = 1 WHERE product.id = #{id} ")
    int putOnSale(Long id);

    @Update("UPDATE product SET status = 0 WHERE product.id = #{id} ")
    int stopSale(Long id);

    @Select("""
    SELECT EXISTS(SELECT 1 FROM spec WHERE product_id = #{id})
    AND
    NOT EXISTS(
    SELECT 1 FROM spec s
             WHERE s.product_id = #{id}
             AND (s.price IS NULL or s.stock <= 0))
    """)
    boolean isExistsErrorSpec(Long id);


    @Select("""
          SELECT
                id,
                snowflake_id,
                name,
                mini_price.price,
                cover_url,
                images,
                description,
                status,
                origin,
                created_at
            FROM product
                     JOIN
                     (SELECT product_id,MIN(price) AS price FROM spec GROUP BY product_id) mini_price
                     ON product.id = mini_price.product_id
            WHERE product.id = #{productId} AND status = 1
    """)
    ProductDetailVO getListProductMQ(Long productId);

    @Select("""
        SELECT
                spec.id,
                spec.product_id,
                product.snowflake_id AS product_snowflake,
                spec.snowflake_id,
                spec.spec,
                spec.price AS sale_price,
                spec.cover_url,
                spec.stock
            FROM spec JOIN product ON spec.product_id = product.id
            WHERE product.id = #{productId} AND  product.status = 1
    """)
    List<SpecRedisDTO> getSpecOne(Long productId);


    @Select("SELECT snowflake_id FROM product WHERE id = #{id}")
    Long getProductSnowFlakeById(Long id);

    @Select("SELECT spec.snowflake_id FROM spec WHERE id = #{id}")
    Long getSpecSnowFlakeById(Long id);


    @Select("SELECT spec.snowflake_id FROM spec WHERE product_id = #{productId}")
    List<Long> getSpecSnowFlakeByIdList(Long productId);


    @Select("""
    SELECT
        p.snowflake_id AS productSnowFlake,
        s.snowflake_id AS specSnowFlake
    FROM spec s JOIN product p ON s.product_id = p.id
    WHERE s.id > #{lastId} AND p.status = 1
    ORDER BY s.id ASC
    LIMIT #{pageSize}
    """)
    List<snowFlakeMap> getSnowFlakeMap(Long lastId,int pageSize);

    @Select("SELECT stock FROM real_stock WHERE product_id = #{productId}")
    List< Integer> getRealStockList(Long productId);

    @Select("SELECT stock FROM spec WHERE product_id = #{productId}")
    List<Integer> getSpecStockList(Long productId);

    @Select("""
    SELECT
           s.id,
           s.name,
           s.contact,
           s.phone,
           s.address,
           s.rating,
           s.is_active
    FROM supplier s
    WHERE id < #{lastId}
    ORDER BY id DESC
    LIMIT #{pageSize}
    """)
    List<SupplierVO> getSupplierList(Integer lastId, Integer pageSize);


    @Insert("""
    INSERT INTO
    purchase_record(spec_id,supplier_id,purchase_price,quantity,total_amount)
    VALUE(#{New.specId},#{New.supplierId},#{New.purchasePrice},#{New.quantity},#{New.totalAmount})
    """)
    int purchase(@Param("New") newProductPurchase New);

    @Select("SELECT COUNT(*) FROM supplier WHERE id = #{supplierId}")
    int isExist(Integer supplierId);

    @Update("UPDATE real_stock SET stock = #{Stock} WHERE spec_id = #{specId}")
    int updateRealStock(@Param("Stock") Integer Stock, @Param("specId") Long specId);

    @Update("""
    UPDATE spec SET purchase_price = #{purchasePrice} WHERE id = #{specId}
    """)
    int updateSpec(BigDecimal purchasePrice, Long specId);

    DashboardDTO getDashboardDTO();

    @MapKey("month")
    List<Map<String, Object>> getProfitDate(Integer targetYear);


    @Insert("""
    INSERT INTO supplier(name, contact, phone, address, rating, is_active)
    VALUE(#{name},#{contact},#{phone},#{address},5,1)
    """)
    int addSupplier(SupplierAddDTO sa);


    int updateSupplier(Integer rating,Integer isActive,Integer id);

}
