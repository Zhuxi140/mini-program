package src.main.java.com.zhuxi.pojo.VO.Car;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "购物车局部更新商品信息")
public class CartNewVO {

    @Schema(description = "商品规格")
    private String spec;
    @Schema(description = "商品价格")
    private BigDecimal price;
    @Schema(description = "商品库存")
    private Integer stock;

    public CartNewVO() {
    }

    public CartNewVO(String spec, BigDecimal price,Integer stock) {
        this.spec = spec;
        this.price = price;
        this.stock = stock;
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
