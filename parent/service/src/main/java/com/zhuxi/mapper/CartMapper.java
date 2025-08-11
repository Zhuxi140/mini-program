package com.zhuxi.mapper;

import org.apache.ibatis.annotations.*;
import  com.zhuxi.pojo.DTO.Cart.CartAddDTO;
import  com.zhuxi.pojo.DTO.Cart.CartRedisDTO;
import  com.zhuxi.pojo.DTO.Cart.CartUpdateDTO;
import  com.zhuxi.pojo.VO.Car.CartNewVO;
import com.zhuxi.pojo.VO.Car.CartVO;

import java.util.List;

@Mapper
public interface CartMapper {

    @Select("""
    SELECT DISTINCT user.id
    FROM user JOIN cart ON user.id = cart.user_id
    WHERE user.id > #{lastId} AND user.last_time >= DATE_SUB(NOW(),INTERVAL 30 DAY)
    ORDER BY user.id
    LIMIT #{pageSize}
    """)
    List<Long> getUserIds(Long lastId, int pageSize);

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
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "cUDto.cartId")
    Boolean insert(@Param("cUDto") CartAddDTO cartAddDTO, @Param("userId")Long userId);

    // 删除
    @Delete("DELETE FROM cart WHERE id = #{cartId}")
    int delete(Long cartId);


    // 查询
    @Select("""
        SELECT
          cart.id,
          product.snowflake_id AS productSnowflake,
          spec.snowflake_id AS specSnowflake,
          cart.quantity,
          product.name,
          spec.spec,
          spec.price,
          product.status,
          product.cover_url
       FROM cart JOIN product ON cart.product_id = product.id
       JOIN spec ON cart.spec_id = spec.id
       WHERE cart.user_id = #{userId} AND cart.id < #{lastId}
       ORDER BY cart.id DESC
       LIMIT #{pageSize}
       """)
    List<CartVO> getListCar(Long lastId, int pageSize, Long userId);

    @Select("""
        SELECT
          cart.id,
          product.snowflake_id AS productSnowflake,
          spec.snowflake_id AS specSnowflake,
          cart.quantity
       FROM cart JOIN product ON cart.product_id = product.id
       JOIN spec ON cart.spec_id = spec.id
       WHERE cart.user_id = #{userId}
       """)
    List<CartRedisDTO> getListCarOne(Long userId);

    @Select("""
    SELECT
          spec.spec,
          spec.price
    FROM spec
    WHERE spec.product_id = #{productId} AND spec.id= #{specId}
    """)
    CartNewVO getNewCar(Long productId, Long specId);


    //清空
    @Delete("DELETE FROM cart WHERE user_id = #{userId}")
    Boolean deleteAll(Long userId);

    @Select("SELECT snowflake_id FROM product WHERE id = #{productId}")
    Long getProductSnowFlakeById(Long productId);


    @Select("SELECT product_id FROM spec WHERE snowflake_id = #{snowflakeId}")
    Long getProductIdBySnowFlake(Long SnowFlake);

    @Select("SELECT id FROM spec WHERE snowflake_id = #{snowflakeId}")
    Long getSpecIdBySnowFlake(Long specSnowFlake);


    @Select("SELECT COUNT(*) FROM cart WHERE spec_id = #{specId} AND user_id = #{userId}")
    Integer getCartCount(Long specId, Long userId);

    @Update("UPDATE cart SET quantity = quantity + #{quantity} WHERE spec_id = #{specId} AND user_id = #{userId}")
    int updateCartStock(Long specId, Long userId, int quantity);

    @Select("SELECT COUNT(*) FROM cart WHERE user_id = #{userId} ")
    Integer getSumCount(Long userId);


    @Select("SELECT id FROM cart WHERE cart.user_id = #{userId} AND cart.spec_id = #{specId}")
    Long getCartIdByUS(Long userId, Long specId);

}
