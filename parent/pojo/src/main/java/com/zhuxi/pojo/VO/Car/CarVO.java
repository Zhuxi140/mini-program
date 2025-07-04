package src.main.java.com.zhuxi.pojo.VO.Car;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public class CarVO {

    @Schema(description = "购物车id")
    private Long id;
    @Schema(description = "商品id")
    private Long productId;
    @Schema(description = "数量")
    private Integer quantity;
    @Schema(description = "商品名")
    private String name;
    @Schema(description = "商品单价")
    private BigDecimal price;
    @Schema(description = "商品主图url")
    private String coverUrl;


    public CarVO(Long id,Long productId, Integer quantity, String name, BigDecimal price, String coverUrl) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.name = name;
        this.price = price;
        this.coverUrl = coverUrl;
    }

    public CarVO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long carId) {
        this.id = carId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String productName) {
        this.name = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
