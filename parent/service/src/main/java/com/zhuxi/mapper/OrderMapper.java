package com.zhuxi.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import src.main.java.com.zhuxi.pojo.DTO.Order.OrderAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.Order.PaymentAddDTO;

import java.math.BigDecimal;

@Mapper
public interface OrderMapper {


    // 添加订单
    int insert(OrderAddDTO orderAddDTO);

    //查询商品售价
    @Select("SELECT price FROM spec WHERE id = #{specId}")
    BigDecimal getProductSalePrice(Long productId);

    //添加订单组
    @Insert("""
    INSERT INTO order_group(group_sn, user_id, total_amount, payment_status)
    VALUES (#{orderSn}, #{userId}, #{totalAmount}, 0)
    """)
     int insertOrderGroup(String orderSn, Long userId, BigDecimal totalAmount);

    //添加支付记录
    @Insert("""
    INSERT INTO payment(payment_sn, user_id, order_id, total_amount,status)
    VALUES (#{paymentSn}, #{userId}, #{orderId}, #{totalAmount},#{status})
    """)
    int insertPayment(PaymentAddDTO paymentAddDTO);

    //获取默认地址
    @Select("SELECT id FROM user_address WHERE user_id = #{userId} AND is_default = 1")
    Long getDefaultAddressId(Long userId);
}
