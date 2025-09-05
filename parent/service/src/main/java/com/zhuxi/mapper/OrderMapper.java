package com.zhuxi.mapper;

import org.apache.ibatis.annotations.*;
import com.zhuxi.pojo.DTO.Order.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {


    @Select("SELECT @@DB_LAST_UPDATED_COUNT")
    int[] getLastBatchUpdateCounts();

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

    @Select("SELECT id FROM `order` WHERE order_sn = #{orderSn} AND status != 7")
    Long getOrderIdDelete(String orderSn);

    //取消订单
    @Update("UPDATE `order` SET status = 4 WHERE id = #{orderId} AND status = 0")
    int cancelOrder(Long orderId);

    //释放预占库存
    @Update("UPDATE inventory_lock SET status = 2 WHERE order_id = #{orderId} AND status = 0")
    int releaseInventoryLock(Long orderId);

    //取消支付
    @Update("UPDATE payment SET status = 4 WHERE order_id = #{orderId} AND status = 0")
    int cancelPayment(Long orderId);

    // 查询需要恢复的数量
    @Select("SELECT i.quantity FROM inventory_lock i WHERE order_id = #{orderId} AND i.status = 0 ")
    Integer getInventoryLockQuantity(Long orderId);

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
    @Update("UPDATE `order` SET status = 7 WHERE id = #{id} AND user_id = #{userId}")
    int deleteOrder(Long id, Long userId);


    @Select("""
    SELECT o.id,order_sn,user_id
    FROM `order` o JOIN user ON o.user_id = user.id
    WHERE o.id > #{lastId} AND last_time >= DATE_SUB(NOW(),INTERVAL 30 DAY)
    ORDER BY o.id
    LIMIT #{pageSize}
    """)
    List<BloomOrderDTO> getAllOrderId(Long lastId, int pageSize);

    @Select("""
    SELECT o.id,order_sn
    FROM `order` o JOIN user ON o.user_id = user.id
    WHERE o.id > #{lastId} AND last_time >= DATE_SUB(NOW(),INTERVAL 30 DAY) AND user_id = #{userId}
    ORDER BY o.id
    LIMIT #{pageSize}
    """)
    List<BloomOrderDTO> getAllOrderIdByOne(Long lastId, int pageSize,Long userId);


    List<OrderRedisDTO> getOrderRedisList(Long userId,Long lastId,int pageSize);

    OrderRedisDTO getOrderRedis(Long userId,String orderSn);

    @Select("""
    SELECT DISTINCT user.id
    FROM user JOIN `order` ON  user.id = `order`.user_id
    WHERE user.id > #{lastId} AND last_time >= DATE_SUB(NOW(),INTERVAL 30 DAY)
    ORDER BY user.id
    LIMIT #{pageSize}
    """)
    List<Long> getUserIds(Long lastId,int pageSize);

    @Select("""
    SELECT id
    FROM user
    WHERE id > #{id} AND last_time >= DATE_SUB(NOW(),INTERVAL 30 DAY)
    ORDER BY id ASC
    LIMIT #{pageSize}
    """)
    List<Long> getUserIdList(Long id,int pageSize);


    @Select("SELECT product_id FROM spec WHERE snowflake_id = #{snowflakeId}")
    Long getProductIdBySnowFlake(Long specSnowFlake);

    @Select("SELECT id FROM spec WHERE snowflake_id = #{snowflakeId}")
    Long getSpecIdBySnowFlake(Long specSnowFlake);


    @Select("SELECT status FROM `order` WHERE id = #{orderId}")
    Integer getOrderStatus(Long orderId);

    List<String> getOrderSnList(List<Long> orderIds);
}
