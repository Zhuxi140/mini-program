package com.zhuxi.pojo.DTO.Cart;

import io.swagger.v3.oas.annotations.media.Schema;

public class CartAddDTO {

    @Schema(hidden = true)
    private Long cartId;
    @Schema(hidden = true)
    private Long productSnowflake ;
    @Schema(description = "规格号",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long specSnowflake;
    @Schema(description = "数量（后台默认添加数量为1）",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity = 1;
    @Schema(hidden = true)
    private Long productId;
    @Schema(hidden = true)
    private Long specId;
    @Schema(hidden = true)
    private Long userId;

    public CartAddDTO() {
    }

    public CartAddDTO(Long cartId, Long productSnowflake, Long specSnowflake, Integer quantity, Long productId, Long specId, Long userId) {
        this.cartId = cartId;
        this.productSnowflake = productSnowflake;
        this.specSnowflake = specSnowflake;
        this.quantity = quantity;
        this.productId = productId;
        this.specId = specId;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }
}
