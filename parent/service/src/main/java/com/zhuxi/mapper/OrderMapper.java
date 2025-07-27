package com.zhuxi.mapper;

import org.apache.ibatis.annotations.*;
import src.main.java.com.zhuxi.pojo.DTO.Order.*;
import src.main.java.com.zhuxi.pojo.VO.Order.OrderShowVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

// ---------------------------------单一商品订单区域--------------------------------------
    // 添加订单
    int insert(OrderAddDTO orderAddDTO);

    //查询商品售价
    @Select("SELECT price FROM spec WHERE id = #{specId}")
    BigDecimal getProductSalePrice(Long productId);

    //添加支付记录
    @Insert("""
    INSERT INTO payment(payment_sn, user_id, order_id, total_amount,status)
    VALUES (#{paymentSn}, #{userId}, #{orderId}, #{totalAmount},#{status})
    """)

    int insertPayment(PaymentAddDTO paymentAddDTO);
    // 创建预占记录
    @Insert("""
    INSERT INTO inventory_lock(product_id,order_id,spec_id,lock_sn,quantity, status, expire_time)
    VALUES (#{productId},#{orderId}, #{specId},#{lockSn}, #{quantity}, 0, DATE_ADD(now(),INTERVAL 30 MINUTE))
    """)
    int insertInventoryLock(Long productId, Long specId, Long orderId, Integer quantity,String lockSn);
    //获取默认地址
    @Select("SELECT id FROM user_address WHERE user_id = #{userId} AND is_default = 1")
    Long getDefaultAddressId(Long userId);

    // 获取商品真实库存
    @Select("SELECT real_stock.stock FROM real_stock WHERE spec_id = #{specId}")
    Integer getProductRealStock(Long specId);

    // 获取商品可售库存
    @Select("SELECT spec.stock FROM spec WHERE id=#{specId}")
    Integer getProductSaleStock(Long specId);

    //获取商品预占库存
    @Select("""
     SELECT COALESCE(SUM(inventory_lock.quantity),0) FROM inventory_lock
       WHERE spec_id = #{specId}
       AND status = 0
       AND expire_time > now()
    """)
    Integer getProductPreStock(Long specId);

    // 可售库存减少(被锁定)
    @Update("""
    UPDATE spec SET stock = stock - #{quantity}
    WHERE id = #{specId} AND stock >= #{quantity}
    """)
    boolean reduceProductSaleStock(Long specId, Integer quantity);

    @Select("SELECT id FROM `order` WHERE order_sn = #{orderSn}")
    Long getOrderId(String orderSn);

    //取消订单
    @Update("UPDATE `order` SET status = 4 WHERE id = #{orderId}")
    int cancelOrder(Long orderId);

    //释放预占库存
    @Update("UPDATE inventory_lock SET status = 2 WHERE order_id = #{orderId}")
    int releaseInventoryLock(Long orderId);

    //取消支付
    @Update("UPDATE payment SET status = 4 WHERE order_id = #{orderId}")
    int cancelPayment(Long orderId);

    // 查询需要恢复的数量
    @Select("SELECT inventory_lock.quantity FROM inventory_lock WHERE order_id = #{orderId} ")
    int getInventoryLockQuantity(Long orderId);

    // 查询对应specId
    @Select("SELECT spec_id FROM inventory_lock WHERE order_id = #{orderId}")
    Long getSpecId(Long orderId);

    // 恢复被锁定的库存
    @Update("""
    UPDATE spec SET stock = stock + #{quantity} WHERE id = #{specId}
    """)
    int releaseProductSaleStock(Long specId, Integer quantity);


// ---------------------------------订单组区域--------------------------------------
    //添加订单组
    @Insert("""
    INSERT INTO order_group(group_sn, user_id, total_amount, payment_status)
    VALUES (#{groupSn}, #{userId}, #{totalAmount}, 0)
    """)
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
     int insertOrderGroup(OrderGroupDTO orderGroupDTO);

    //批量获取商品真实库存
    List<Integer> getProductRealStockList(List<Long> specIds);

    // 批量获取商品可售库存
    List<Integer> getProductSaleStockList(List<Long> specIds);

    //批量获取商品预占库存
    List<Integer> getProductPreStockList(List<Long> specIds);

    // 批量添加订单
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertOrderList(List<OrderAddDTO> orderAddDTO);

    // 批量添加支付记录
    int insertPaymentList(List<PaymentAddDTO> paymentAddDTO);

    // 批量添加库存预占
    int insertInventoryLockList(List<InventoryLockAddDTO> inventoryLockAddDTOS);

    // 批量减少可售库存
    int reduceProductSaleStockList(List<Long> specIds, List<Integer> quantity);

    // 获取第一条插入的id
    @Select("SELECT LAST_INSERT_ID()")
    Long getLastInsertId();

    // 获取订单id列表
    @Select("SELECT id FROM `order` WHERE group_id = #{groupId}")
    List<Long> getOrderIdList(Long groupId);

    //批量取消订单
    int cancelOrderList(List<Long> orderIds);

    //批量取消支付
    int cancelPaymentList(List<Long> orderIds);

    //批量释放库存
    int releaseInventoryLockList(List<Long> orderIds);

    // 批量查询需要恢复的数量
    List<Integer> getInventoryLockQuantityList(List<Long> orderIds);

    // 批量查询对应的specId
    List<Long> getSpecIdList(List<Long> orderIds);

    //批量恢复被锁定的库存
    int releaseProductSaleStockList(List<Long> specIds, List<Integer> quantityList);


    // 展示订单列表
    List<OrderRedisDTO> getOrderList(Long userId, LocalDateTime createdAt, Integer pageSize);



    //删除订单
    @Update("UPDATE `order` SET status = 7 WHERE order_sn = #{orderSn} AND user_id = #{userId}")
    int deleteOrder(String orderSn, Long userId);


    @Select("SELECT id,user_id FROM `order` WHERE id > #{lastId}  ORDER BY id LIMIT #{pageSize}")
    List<BloomOrderDTO> getAllOrderId(Long lastId, int pageSize);


    List<OrderRedisDTO> getOrderRedisList(Long userId,Long lastId,int pageSize);

    @Select("SELECT id FROM user WHERE id > #{lastId} ORDER BY id LIMIT #{pageSize}")
    List<Long> getUserIds(Long lastId,int pageSize);

}
