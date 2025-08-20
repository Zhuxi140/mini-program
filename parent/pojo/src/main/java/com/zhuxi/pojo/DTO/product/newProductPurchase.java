package com.zhuxi.pojo.DTO.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public class newProductPurchase {

    @JsonIgnore
    private Long id;
//    @Schema(description = "商品id",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonIgnore
    private Long productId;
    @Schema(description = "商品规格id",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long specId;
    @Schema(description = "供应商id",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer supplierId;
    @Schema(description = "采购价格",requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal purchasePrice;
    @Schema(description = "采购数量",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;
    @JsonIgnore
    @Schema(description = "采购总价",hidden = true)
    private BigDecimal totalAmount;

    public newProductPurchase() {
    }

    public newProductPurchase(Long id, Long productId, Long specId, Integer supplierId, BigDecimal purchasePrice, Integer quantity, BigDecimal totalAmount) {
        this.id = id;
        this.productId = productId;
        this.specId = specId;
        this.supplierId = supplierId;
        this.purchasePrice = purchasePrice;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
