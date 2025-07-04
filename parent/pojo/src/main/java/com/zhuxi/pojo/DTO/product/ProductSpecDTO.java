package src.main.java.com.zhuxi.pojo.DTO.product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public class ProductSpecDTO {

    @Schema(description = "商品规格(至少有一个规格)",requiredMode = Schema.RequiredMode.REQUIRED)
    private String spec;
    @Schema(description = "商品价格",requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal price;
    @Schema(description = "规格展示图",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String coverUrl = null;
    @Schema(description = "商品库存(默认为1)",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer stock = 1;

    public ProductSpecDTO(String spec, BigDecimal price, String coverUrl, Integer stock) {
        this.spec = spec;
        this.price = price;
        this.coverUrl = coverUrl;
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

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
