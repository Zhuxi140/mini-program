package src.main.java.com.zhuxi.pojo.DTO.product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public class ProductSpecUpdateDTO {
    @Schema(description = "规格id",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;
    @Schema(description = "规格")
    private String spec;
    @Schema(description = "采购价格",hidden = true)
    private BigDecimal purchase_price;
    @Schema(description = "售价")
    private BigDecimal price;
    @Schema(description = "可售库存")
    private Integer stock;


    public ProductSpecUpdateDTO() {
    }

    public ProductSpecUpdateDTO(Long id, String spec,BigDecimal purchase_price, BigDecimal price,Integer stock) {
        this.id = id;
        this.spec = spec;
        this.purchase_price = purchase_price;
        this.price = price;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPurchase_price() {
        return purchase_price;
    }

    public void setPurchase_price(BigDecimal purchase_price) {
        this.purchase_price = purchase_price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
