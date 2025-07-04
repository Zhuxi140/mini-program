package com.zhuxi.mapper;


import org.apache.ibatis.annotations.*;
import src.main.java.com.zhuxi.pojo.DTO.product.ProductUpdateDTO;
import src.main.java.com.zhuxi.pojo.DTO.product.ProductBaseDTO;
import src.main.java.com.zhuxi.pojo.DTO.product.ProductSpecDTO;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminProductVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductOverviewVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductSpecVO;

import java.util.List;

@Mapper
public interface ProductMapper {

    List<ProductOverviewVO> getListProducts(Long lastId, Integer pageSize);

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

    @Select("SELECT spec,price,cover_url,stock FROM spec WHERE product_id = #{id}")
    List<ProductSpecVO> getProductSpec(Long id);

    // 添加商品基础信息
    @Insert("""
    INSERT INTO product(name,cover_url,images,description,origin,status)
    VALUES (#{name},#{coverUrl},#{images},#{description},#{origin},#{status})
    """)
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    Boolean addBase(ProductBaseDTO productBaseDTO);

    //添加规格信息
    Boolean addSpec(@Param("list") List<ProductSpecDTO> productSpecDTO,@Param("product_id")  Long id);

    // 修改商品基础信息
    int updateProductBase(@Param("base") ProductBaseDTO productBaseDTO);

    // 修改商品规格信息
    int updateProductSpec(@Param("list") List<ProductSpecDTO> productSpecDTO,@Param("product_id")  Long id);

    // 删除商品基础信息
    @Delete("DELETE FROM product WHERE id = #{id}")
    int deleteProductBase(Long id);

    // 删除商品规格信息
    @Delete("DELETE FROM spec WHERE product_id = #{id}")
    int deleteProductSpec(Long id);
}
