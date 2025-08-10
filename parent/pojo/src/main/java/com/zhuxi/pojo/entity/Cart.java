package com.zhuxi.pojo.entity;

public class Cart {
    private Integer id;
    private Integer userId;
    private Long specId;
    private Integer productId;
    private Integer quantity;

    public Cart() {
    }

    public Cart(Integer id, Integer userId, Long specId, Integer productId, Integer quantity) {
        this.id = id;
        this.userId = userId;
        this.specId = specId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getSpecId() {
        return specId;
    }

    public void setSpecId(Long specId) {
        this.specId = specId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
