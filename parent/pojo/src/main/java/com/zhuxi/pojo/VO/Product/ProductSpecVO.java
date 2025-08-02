package src.main.java.com.zhuxi.pojo.VO.Product;

import java.math.BigDecimal;

public class ProductSpecVO {
    private Long SpecNumber;
    private String spec;
    private BigDecimal price;
    private String coverUrl;

    public ProductSpecVO() {
    }

    public ProductSpecVO(Long specNumber, String spec, BigDecimal price, String coverUrl) {
        SpecNumber = specNumber;
        this.spec = spec;
        this.price = price;
        this.coverUrl = coverUrl;
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

    public Long getSpecNumber() {
        return SpecNumber;
    }

    public void setSpecNumber(Long specNumber) {
        SpecNumber = specNumber;
    }
}
