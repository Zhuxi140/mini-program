package com.zhuxi.pojo.DTO.Cart;

import io.swagger.v3.oas.annotations.media.Schema;

public class CartUpdateDTO {
    @Schema(description = "购物车id",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long cartId ;
    @Schema(description = "规格id",hidden = true)
    private Long specId;
    @Schema(description = "规格号",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long specSnowflake;
    @Schema(description = "数量（若为第一次加入购物车，不需填写）",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer quantity = 1;
    @Schema(hidden = true)
    private Long userId;

    public CartUpdateDTO() {
    }

    public CartUpdateDTO(Long cartId, Long specId, Long specSnowflake, Integer quantity, Long userId) {
        this.cartId = cartId;
        this.specId = specId;
        this.specSnowflake = specSnowflake;
        this.quantity = quantity;
        this.userId = userId;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Long getSpecId() {
        return specId;
    }

    public void setSpecId(Long specId) {
        this.specId = specId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getSpecSnowflake() {
        return specSnowflake;
    }

    public void setSpecSnowflake(Long specSnowflake) {
        this.specSnowflake = specSnowflake;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
