package src.main.java.com.zhuxi.pojo.DTO.product;

import java.math.BigDecimal;
import java.util.List;

public class ProductUpdateDTO {
    private String name;
    private BigDecimal price;
    private String coverUrl;
    private List<String> images;
    private String description;
    private Integer stock;
    private String origin;
    private Integer status;

    public ProductUpdateDTO(String name, BigDecimal price, String coverUrl, List<String> images, String description, Integer stock, String origin, Integer status) {
        this.name = name;
        this.price = price;
        this.coverUrl = coverUrl;
        this.images = images;
        this.description = description;
        this.stock = stock;
        this.origin = origin;
        this.status = status;
    }

    public ProductUpdateDTO() {
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

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
