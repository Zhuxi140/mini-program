package com.zhuxi.mapper;


import org.apache.ibatis.annotations.*;
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

@Mapper
public interface ProductMapper {

    List<ProductOverviewVO> getListProductByCreate(LocalDateTime dateTime, Integer pageSize, Long lastId);

    List<ProductOverviewVO> getListProductByPriceDESC(BigDecimal price, Integer pageSize, Long lastId);

    List<ProductOverviewVO> getListProductByPriceASC(BigDecimal price, Integer pageSize, Long lastId);

    List<ProductDetailVO> getListProduct(Long lastId, Integer pageSize);

    List<AdminProductVO>  getListAdminProductsDESC(Long lastId , Integer pageSize);

    List<AdminProductVO>  getListAdminProductsASC(Long lastId , Integer pageSize);


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

    @Select("""
    SELECT spec.id AS specId,
           spec.spec,
           spec.price AS sale_price,
           spec.purchase_price,
           spec.stock,
           real_stock.stock AS real_stock
    FROM spec JOIN real_stock ON real_stock.product_id = #{productId}
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

    //初始化真实库存记录
    Boolean addRealStock(@Param("list") List<RealStockDTO> realStockDTO);


    // 修改商品基础信息
    int updateProductBase(@Param("base") ProductBaseUpdateDTO productBaseUpdateDTO);

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

    // 添加规格图
    @Insert("""
    UPDATE spec SET cover_url = #{coverUrl} WHERE product_id = #{productID} AND id = #{id}
    """)
    int addSpecCoverUrl(String coverUrl, Long productID, Long id);

    //添加商品 封面图、详细图
    int addBasePics(String coverUrl,@Param("image") List<String> images, Long productId);



    @Select("SELECT id FROM product WHERE id > #{lastId} ORDER BY id LIMIT #{pageSize}")
    List<Long> getAllProductId(Long lastId,int pageSize);
}
