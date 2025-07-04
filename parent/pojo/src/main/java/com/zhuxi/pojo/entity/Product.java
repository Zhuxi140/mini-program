package src.main.java.com.zhuxi.pojo.entity;

import java.math.BigDecimal;
import java.util.List;

public class Product {
    private Integer id;
    private String name;
    private BigDecimal price;
    private String spec;
    private String coverUrl;
    private List<String> images;
    private String description;
    private Integer stock;
    private String origin;
    private Integer status;

    public Product(Integer id, String name, BigDecimal price,String spec,String coverUrl, List<String> images, String description, Integer stock, String origin, Integer status) {

        this.id = id;
        this.name = name;
        this.price = price;
        this.spec = spec;
        this.coverUrl = coverUrl;
        this.images = images;
        this.description = description;
        this.stock = stock;
        this.origin = origin;
        this.status = status;
    }

    public Product() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
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
