package com.zhuxi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import src.main.java.com.zhuxi.pojo.DTO.Pay.PayDTO;

@Mapper
public interface PayMapper {

    int updatepayment(PayDTO payDTO);

    @Update("""
    UPDATE `order`
    SET
    status = 1
    WHERE
     `order`.id = #{orderId} AND `order`.user_id = #{userId}
    """)
    int updateOrderStatus(Long orderId,Long userId);

    @Update("""
    UPDATE inventory_lock
    SET
    status = 1
    WHERE order_id = #{orderId}
    """)
    int updateInventory(Long orderId);

}
