package src.main.java.com.zhuxi.pojo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {

    private Long id;
    private String OrderSn; // 订单编号(日期+6位随机数)
    private Long userId;
    private Long addressId;
    private BigDecimal totalAmount;
    private Integer status;    // 0:待付款 1:已付款  2: 已取消
    private LocalDateTime createdAt;

    public Order(Long id, String orderSn, Long userId, Long addressId, BigDecimal totalAmount, Integer status, LocalDateTime createdAt) {
        this.id = id;
        OrderSn = orderSn;
        this.userId = userId;
        this.addressId = addressId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Order() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderSn() {
        return OrderSn;
    }

    public void setOrderSn(String orderSn) {
        OrderSn = orderSn;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
