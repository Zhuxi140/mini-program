package com.zhuxi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import src.main.java.com.zhuxi.pojo.DTO.Pay.PayDTO;

@Mapper
public interface PayMapper {

    int updatepayment(PayDTO payDTO);

    @Update("""
    UPDATE `order` o
    SET
    status = 1
    WHERE
     o.id = #{orderId} AND o.user_id = #{userId} AND o.status = 0
    """)
    int updateOrderStatus(Long orderId,Long userId);

    @Update("""
    UPDATE inventory_lock
    SET
    status = 1
    WHERE order_id = #{orderId} AND status = 0
    """)
    int updateInventory(Long orderId);

    @Select("SELECT id FROM `order` WHERE order_sn = #{orderSn}")
    Long getOrderIdByOrderSn(String orderSn);

    @Select("SELECT spec_id FROM `order` WHERE id = #{id}")
    Long getSpecIdByOrderId(Long id);

    @Update("UPDATE real_stock SET stock = stock - #{quantity} WHERE spec_id = #{specId} AND stock >= #{quantity}")
    int updateRealStock(Long specId,Integer quantity);

}
