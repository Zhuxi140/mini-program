package com.zhuxi.pojo.entity;

import java.math.BigDecimal;

public class Order {

    private Long id;
    private String OrderSn; // 订单编号(日期+6位随机数)
    private Long userId;
    private Long productId;
    private Long specId;
    private Long addressId;
    private Integer productQuantity;
    private BigDecimal totalAmount;
    private Integer status;


    public Order() {
    }

    public Order(Long id, String orderSn, Long userId, Long productId, Long specId, Long addressId, Integer productQuantity, BigDecimal totalAmount, Integer status) {
        this.id = id;
        OrderSn = orderSn;
        this.userId = userId;
        this.productId = productId;
        this.specId = specId;
        this.addressId = addressId;
        this.productQuantity = productQuantity;
        this.totalAmount = totalAmount;
        this.status = status;
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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getSpecId() {
        return specId;
    }

    public void setSpecId(Long specId) {
        this.specId = specId;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Integer getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Integer productQuantity) {
        this.productQuantity = productQuantity;
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
}
