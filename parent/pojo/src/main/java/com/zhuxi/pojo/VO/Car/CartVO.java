package src.main.java.com.zhuxi.pojo.VO.Car;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public class CartVO {

    @Schema(description = "购物车id")
    private Long id;
    @Schema(description = "商品号")
    private Long productSnowflake;
    @Schema(description = "规格号")
    private Long specSnowflake;
    @Schema(description = "购买数量")
    private Integer quantity;
    @Schema(description = "商品主名")
    private String name;
    @Schema(description = "规格名")
    private String spec;
    @Schema(description = "库存")
    private Integer stock;
    @Schema(description = "商品单价")
    private BigDecimal price;
    @Schema(description = "商品状态",hidden = true)
    private Integer status;
    @Schema(description = "商品主图url")
    private String coverUrl;


    public CartVO() {
    }

    public CartVO(Long id,Long productSnowflake,Long specSnowflake, Integer quantity, String name, String spec, Integer stock, BigDecimal price, Integer status, String coverUrl) {
        this.id = id;
        this.productSnowflake = productSnowflake;
        this.specSnowflake = specSnowflake;
        this.quantity = quantity;
        this.name = name;
        this.spec = spec;
        this.stock = stock;
        this.price = price;
        this.status = status;
        this.coverUrl = coverUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
