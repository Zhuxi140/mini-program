package com.zhuxi.pojo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class purchaseRecord {
    private Long id;
    private Long productId;
    private Long specId;
    private Integer supplierId;
    private BigDecimal purchasePrice;
    private Integer quantity;
    private BigDecimal totalAmount;
    private LocalDateTime purchaseDate;

    public purchaseRecord() {
    }

    public purchaseRecord(Long id, Long productId, Long specId, Integer supplierId, BigDecimal purchasePrice, Integer quantity, BigDecimal totalAmount, LocalDateTime purchaseDate) {
        this.id = id;
        this.productId = productId;
        this.specId = specId;
        this.supplierId = supplierId;
        this.purchasePrice = purchasePrice;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.purchaseDate = purchaseDate;
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

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
