package com.zhuxi.mapper;

import org.apache.ibatis.annotations.*;
import src.main.java.com.zhuxi.pojo.DTO.Car.CarUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Car.CarVO;

import java.util.List;

@Mapper
public interface CartMapper {

    @Update("update cart_view set quantity = #{carUpdateDTO.quantity} where user_id = #{userId} AND product_id = #{carUpdateDTO.productId}")
    int updateQuantity(@Param("carUpdateDTO")CarUpdateDTO carUpdateDTO, @Param("userId") Long userId);

    @Insert("insert into cart(user_id, product_id, quantity) values(#{userId}, #{carUpdateDTO.productId}, #{carUpdateDTO.quantity})")
    Boolean insert(@Param("carUpdateDTO")CarUpdateDTO carUpdateDTO, @Param("userId")Long userId);

    @Delete("DELETE FROM cart WHERE user_id = #{userId} AND product_id = #{productId}")
    Boolean delete(Long userId, Long productId);

    @Select("""
        SELECT cart_id,product_id,quantity,product_name,price,cover_url
        FROM cart_view
        WHERE user_id = #{userId}
        """)
    List<CarVO> getListCar(Long userId);


    //清空
    @Delete("DELETE FROM cart WHERE user_id = #{userId}")
    Boolean deleteAll(Long userId);
}
