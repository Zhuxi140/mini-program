package src.main.java.com.zhuxi.pojo.DTO.product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public class ProductSpecDTO {

    @Schema(description = "商品规格(至少有一个规格)",requiredMode = Schema.RequiredMode.REQUIRED)
    private String spec;
    @Schema(description = "商品价格",requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal price;
    @Schema(description = "商品库存(默认为1)",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer stock = 1;

    public ProductSpecDTO(String spec, BigDecimal price, Integer stock) {
        this.spec = spec;
        this.price = price;
        this.stock = stock;
    }

    public ProductSpecDTO() {
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

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
