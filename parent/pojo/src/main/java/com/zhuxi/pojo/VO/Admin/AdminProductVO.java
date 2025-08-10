package com.zhuxi.pojo.VO.Admin;


import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public class AdminProductVO {
    @Schema(description = "商品id")
    private Long id;
    @Schema(description = "商品名称")
    private String name;
    @Schema(description = "商品封面图片")
    private String coverUrl;
    @Schema(description = "供应商名称")
    private String supplierName;
    @Schema(description = "商品规格数量")
    private Long specCount;
    @Schema(description = "最低价格")
    private BigDecimal minPrice;
    @Schema(description = "最高价格")
    private BigDecimal maxPrice;
    @Schema(description = "总库存")
    private Long totalRealStock;
    @Schema(description = "在售数量")
    private Long onSaleStock;
    @Schema(description = "库存状态 1为紧张(即低于100) 0为充裕(大于100)")
    private String stockStatus;

    public AdminProductVO() {
    }

    public AdminProductVO(Long id, String name, String coverUrl, String supplierName, Long specCount, BigDecimal minPrice, BigDecimal maxPrice, Long totalRealStock, Long onSaleStock, String stockStatus) {
        this.id = id;
        this.name = name;
        this.coverUrl = coverUrl;
        this.supplierName = supplierName;
        this.specCount = specCount;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.totalRealStock = totalRealStock;
        this.onSaleStock = onSaleStock;
        this.stockStatus = stockStatus;
    }

    public Long getOnSaleStock() {
        return onSaleStock;
    }

    public void setOnSaleStock(Long onSaleStock) {
        this.onSaleStock = onSaleStock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Long getSpecCount() {
        return specCount;
    }

    public void setSpecCount(Long specCount) {
        this.specCount = specCount;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Long getTotalRealStock() {
        return totalRealStock;
    }

    public void setTotalRealStock(Long totalRealStock) {
        this.totalRealStock = totalRealStock;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }
}
