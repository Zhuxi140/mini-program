package com.zhuxi.mapper;

import org.apache.ibatis.annotations.*;
import src.main.java.com.zhuxi.pojo.DTO.Car.CarAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.Car.CartUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Car.CartNewVO;
import src.main.java.com.zhuxi.pojo.VO.Car.CartVO;

import java.util.List;

@Mapper
public interface CartMapper {

    // 修改购物车商品数量或规格
    int updateQuantityOrSpec(@Param("cUDto") CartUpdateDTO cartUpdateDTO, @Param("userId") Long userId);


    @Select("""
    SELECT spec.stock FROM spec WHERE product_id = #{productId} AND id = #{specId}
    """)
    Integer getStock(Long productId, Long specId);

    @Insert("""
    insert into cart(user_id,spec_id, product_id, quantity)
    values(#{userId},#{cUDto.specId},#{cUDto.productId}, #{cUDto.quantity})
    """)
    Boolean insert(@Param("cUDto") CarAddDTO carAddDTO, @Param("userId")Long userId);

    // 删除
    @Delete("DELETE FROM cart WHERE id = #{cartId}")
    Boolean delete(Long cartId);


    // 查询
    @Select("""
        SELECT
          cart.id,
          cart.product_id,
          cart.spec_id,
          cart.quantity,
          product.name,
          spec.spec,
          spec.stock,
          spec.price,
          product.status,
          product.cover_url
       FROM cart JOIN product ON cart.product_id = product.id
       JOIN spec
       ON cart.spec_id = spec.id
       WHERE cart.user_id = #{userId}
       """)
    List<CartVO> getListCar(Long userId);

    @Select("""
    SELECT
          spec.spec,
          spec.price,
          spec.stock
    FROM spec
    WHERE spec.product_id = #{productId} AND spec.id= #{specId}
    """)
    CartNewVO getNewCar(Long productId, Long specId);


    //清空
    @Delete("DELETE FROM cart WHERE user_id = #{userId}")
    Boolean deleteAll(Long userId);
}
