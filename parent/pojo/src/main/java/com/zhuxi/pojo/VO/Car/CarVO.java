package src.main.java.com.zhuxi.pojo.VO.Car;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public class CarVO {

    @Schema(description = "购物车id")
    private Long cartId;
    @Schema(description = "商品id")
    private Long productId;
    @Schema(description = "数量")
    private Integer quantity;
    @Schema(description = "商品名")
    private String productName;
    @Schema(description = "商品单价")
    private BigDecimal price;
    @Schema(description = "商品主图url")
    private String coverUrl;


    public CarVO(Long carId,Long productId, Integer quantity, String productName, BigDecimal price, String coverUrl) {
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
        this.productName = productName;
        this.price = price;
        this.coverUrl = coverUrl;
    }

    public CarVO() {
    }

    public Long getCarId() {
        return cartId;
    }

    public void setCarId(Long carId) {
        this.cartId = carId;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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
