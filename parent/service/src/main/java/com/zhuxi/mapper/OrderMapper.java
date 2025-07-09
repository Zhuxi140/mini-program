package com.zhuxi.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import src.main.java.com.zhuxi.pojo.DTO.Order.InventoryLockAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.Order.OrderAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.Order.OrderGroupDTO;
import src.main.java.com.zhuxi.pojo.DTO.Order.PaymentAddDTO;

import java.math.BigDecimal;
import java.util.List;

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
    VALUES (#{groupSn}, #{userId}, #{totalAmount}, 0)
    """)
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
     int insertOrderGroup(OrderGroupDTO orderGroupDTO);

    //添加支付记录
    @Insert("""
    INSERT INTO payment(payment_sn, user_id, order_id, total_amount,status)
    VALUES (#{paymentSn}, #{userId}, #{orderId}, #{totalAmount},#{status})
    """)
    int insertPayment(PaymentAddDTO paymentAddDTO);

    // 创建预占记录
    @Insert("""
    INSERT INTO inventory_lock(product_id,order_id,spec_id,  quantity, status, expire_time)
    VALUES (#{productId},#{orderId}, #{specId}, #{quantity}, 0, DATE_ADD(now(),INTERVAL 30 MINUTE))
    """)
    int insertInventoryLock(Long productId, Long specId, Long orderId, Integer quantity);
    //获取默认地址
    @Select("SELECT id FROM user_address WHERE user_id = #{userId} AND is_default = 1")
    Long getDefaultAddressId(Long userId);

    // 获取商品真实库存
    @Select("SELECT real_stock.stock FROM real_stock WHERE spec_id = #{specId}")
    Integer getProductRealStock(Long specId);

    //批量获取商品真实库存
    List<Integer> getProductRealStockList(List<Long> specIds);

    // 获取商品可售库存
    @Select("SELECT spec.stock FROM spec WHERE id=#{specId}")
    Integer getProductSaleStock(Long specId);

    // 批量获取商品可售库存
    List<Integer> getProductSaleStockList(List<Long> specIds);

    //获取商品预占库存
    @Select("""
     SELECT COALESCE(SUM(inventory_lock.quantity),0) FROM inventory_lock
       WHERE spec_id = #{specId}
       AND status = 0
       AND expire_time > now()
    """)
    Integer getProductPreStock(Long specId);

    //批量获取商品预占库存
    List<Integer> getProductPreStockList(List<Long> specIds);


    // 批量添加订单
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertOrderList(List<OrderAddDTO> orderAddDTO);

    // 批量添加支付记录
    int insertPaymentList(List<PaymentAddDTO> paymentAddDTO);

    // 批量添加库存预占
    int insertInventoryLockList(List<InventoryLockAddDTO> inventoryLockAddDTOS);


    @Select("SELECT LAST_INSERT_ID()")
    Long getLastInsertId();


}
