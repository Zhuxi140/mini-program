package com.zhuxi.pojo.DTO.Order;

public class OrderMqDTO {
    private String orderSn;
    private Long userId;

    public OrderMqDTO(String orderSn, Long userId) {
        this.orderSn = orderSn;
        this.userId = userId;
    }

    public OrderMqDTO() {
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
