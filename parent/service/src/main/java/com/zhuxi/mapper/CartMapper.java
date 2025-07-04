package com.zhuxi.mapper;

import org.apache.ibatis.annotations.*;
import src.main.java.com.zhuxi.pojo.DTO.Car.CarUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Car.CarVO;

import java.util.List;

@Mapper
public interface CartMapper {

    // 修改
    @Update("""
    update cart set quantity = #{carUpdateDTO.quantity} where user_id = #{userId} AND product_id = #{carUpdateDTO.productId}
""")
    int updateQuantity(@Param("carUpdateDTO")CarUpdateDTO carUpdateDTO, @Param("userId") Long userId);

    @Insert("insert into cart(user_id, product_id, quantity) values(#{userId}, #{carUpdateDTO.productId}, #{carUpdateDTO.quantity})")
    Boolean insert(@Param("carUpdateDTO")CarUpdateDTO carUpdateDTO, @Param("userId")Long userId);

    @Delete("DELETE FROM cart WHERE user_id = #{userId} AND product_id = #{productId}")
    Boolean delete(Long userId, Long productId);


    /*  SELECT cart_id,product_id,quantity,product_name,price,cover_url
        FROM cart_view
        WHERE user_id = #{userId}*/
    // 查询
    @Select("""
  
        SELECT 
          cart.id,
          cart.product_id,
          cart.quantity,
          product.name,
          min_prices.price,
          product.cover_url
       FROM cart JOIN product ON cart.product_id = product.id
       JOIN (
            SELECT product_id,MIN(price) AS price
            FROM spec GROUP BY product_id
            )  min_prices
       ON product.id = min_prices.product_id
       WHERE cart.user_id = #{userId}
       """)
    List<CarVO> getListCar(Long userId);


    //清空
    @Delete("DELETE FROM cart WHERE user_id = #{userId}")
    Boolean deleteAll(Long userId);
}
