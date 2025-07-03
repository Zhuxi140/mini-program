package com.zhuxi.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import src.main.java.com.zhuxi.pojo.DTO.product.ProductAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.product.ProductUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminProductVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductOverviewVO;

import java.util.List;

@Mapper
public interface ProductMapper {

    List<ProductOverviewVO> getListProducts(Long lastId, Integer pageSize);

    List<AdminProductVO>  getListAdminProductsDESC(Long lastId , Integer pageSize);

    List<AdminProductVO>  getListAdminProductsASC(Long lastId , Integer pageSize);

    @Select("select name, price, cover_url, images, description, stock, origin from product where id = #{id}")
    ProductDetailVO getProductDetail(Long id);

    // 添加商品
    @Insert("""
    INSERT INTO product(name,price,cover_url,images,description,stock,origin)
    VALUES (#{name},#{price},#{coverUrl},#{images},#{description},#{stock},#{origin})
    """)
    Boolean add(ProductAddDTO productAddDTO);

    // 修改商品
    int updateProduct(ProductUpdateDTO productUpdateDTO, Long id);
}
