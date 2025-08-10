package com.zhuxi.pojo.VO.Product;

import java.math.BigDecimal;
import java.util.List;

public class ProductVO {

    private Long id;
    private String name;
    private BigDecimal price;
    private List<String> images;
    private String description;
    private String coverUrl;
    private Integer status;
    private String origin;


    public ProductVO(String origin, Integer status, String coverUrl, String description, List<String> images, BigDecimal price, String name, Long id) {
        this.origin = origin;
        this.status = status;
        this.coverUrl = coverUrl;
        this.description = description;
        this.images = images;
        this.price = price;
        this.name = name;
        this.id = id;
    }

    public ProductVO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
