package com.zhuxi.pojo.DTO.Order;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public class OrderAddDTO {

    @Schema(description = "订单id",hidden = true)
    public Long id;
    @Schema(description = "订单编号",hidden = true)
    private String orderSn;
    @Schema(description = "分组id",hidden = true)
    private Long groupId;
    @Schema(description = "用户id",hidden = true)
    private Long userId;
    @Schema(description = "商品id",hidden = true)
    private Long productId;
    @Schema(description = "商品规格id",hidden = true)
    private Long specId;
    @Schema(description = "商品号",/*requiredMode = Schema.RequiredMode.REQUIRED*/ hidden = true)
    private Long productSnowflake;
    @Schema(description = "商品规格号",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long specSnowflake;
    @Schema(description = "收货地址id(自动使用默认,除非前端有传入)",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long addressId;
    @Schema(description = "商品数量(默认为1，除非前端传入)",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer productQuantity = 1;
    @Schema(description = "订单总价",requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal totalAmount;
    @Schema(description = "订单状态(0未支付，1已支付，2已发货，3已完成，4已取消)",hidden = true)
    private Integer status = 0;

    public OrderAddDTO() {
    }

    public OrderAddDTO(Long id, String orderSn, Long groupId, Long userId, Long productId, Long productSnowflake, Long specId, Long specSnowflake, Long addressId, Integer productQuantity, BigDecimal totalAmount, Integer status) {
        this.id = id;
        this.orderSn = orderSn;
        this.groupId = groupId;
        this.userId = userId;
        this.productId = productId;
        this.productSnowflake = productSnowflake;
        this.specId = specId;
        this.specSnowflake = specSnowflake;
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
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
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

    public Long getProductSnowflake() {
        return productSnowflake;
    }

    public void setProductSnowflake(Long productSnowflake) {
        this.productSnowflake = productSnowflake;
    }

    public Long getSpecSnowflake() {
        return specSnowflake;
    }

    public void setSpecSnowflake(Long specSnowflake) {
        this.specSnowflake = specSnowflake;
    }
}
