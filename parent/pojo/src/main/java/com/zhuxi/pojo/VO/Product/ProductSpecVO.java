package src.main.java.com.zhuxi.pojo.VO.Product;

import java.math.BigDecimal;

public class ProductSpecVO {
    private Long specId;
    private String spec;
    private BigDecimal price;
    private String coverUrl;
    private Integer stock;

    public ProductSpecVO() {
    }

    public ProductSpecVO(Long specId, String spec, BigDecimal price, String coverUrl, Integer stock) {
        this.specId = specId;
        this.spec = spec;
        this.price = price;
        this.coverUrl = coverUrl;
        this.stock = stock;
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
