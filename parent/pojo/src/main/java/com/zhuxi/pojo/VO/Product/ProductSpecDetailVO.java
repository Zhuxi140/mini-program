package com.zhuxi.pojo.VO.Product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public class ProductSpecDetailVO {

    @Schema(description = "规格id")
    private Long specId;
    @Schema(description = "规格")
    private String spec;
    @Schema(description = "销售价")
    private BigDecimal salePrice;
    @Schema(description = "采购价")
    private BigDecimal purchasePrice;
    @Schema(description = "正在销售数量")
    private Integer stock;
    @Schema(description = "真实库存")
    private Integer realStock;

    public ProductSpecDetailVO() {
    }

    public ProductSpecDetailVO(Long specId, String spec, BigDecimal salePrice, BigDecimal purchasePrice, Integer stock, Integer realStock) {
        this.specId = specId;
        this.spec = spec;
        this.salePrice = salePrice;
        this.purchasePrice = purchasePrice;
        this.stock = stock;
        this.realStock = realStock;
    }

    public Long getSpecId() {
        return specId;
    }

    public void setSpecId(Long specId) {
        this.specId = specId;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getRealStock() {
        return realStock;
    }

    public void setRealStock(Integer realStock) {
        this.realStock = realStock;
    }
}
