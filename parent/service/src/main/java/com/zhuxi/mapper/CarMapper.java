package com.zhuxi.mapper;

import org.apache.ibatis.annotations.*;
import src.main.java.com.zhuxi.pojo.DTO.Car.CarUpdateDTO;

@Mapper
public interface CarMapper {

    @Update("update cart set quantity = #{carUpdateDTO.quantity} where user_id = #{userId} AND product_id = #{carUpdateDTO.productId}")
    int updateQuantity(@Param("carUpdateDTO")CarUpdateDTO carUpdateDTO, @Param("userId") Long userId);

    @Insert("insert into cart(user_id, product_id, quantity) values(#{userId}, #{carUpdateDTO.productId}, #{carUpdateDTO.quantity})")
    Boolean insert(@Param("carUpdateDTO")CarUpdateDTO carUpdateDTO, @Param("userId")Long userId);

    @Delete("DELETE FROM cart WHERE user_id = #{userId} AND product_id = #{productId}")
    Boolean delete(Long userId, Long productId);
}
