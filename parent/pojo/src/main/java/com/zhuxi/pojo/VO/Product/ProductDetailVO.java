package src.main.java.com.zhuxi.pojo.VO.Product;

import java.math.BigDecimal;
import java.util.List;

public class ProductDetailVO {

    private String name;
    private BigDecimal price;
    private String coverUrl;
    private List<String> images;
    private String description;
    private Integer status;
    private String origin;

    public ProductDetailVO(String name, BigDecimal price,String coverUrl, List<String> images, String description,Integer status,  String origin) {
        this.name = name;
        this.price = price;
        this.images = images;
        this.coverUrl = coverUrl;
        this.status = status;
        this.description = description;
        this.origin = origin;
    }

    public ProductDetailVO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
