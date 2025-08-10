package com.zhuxi.pojo.entity;

import java.util.List;

public class Product {
    private Integer id;
    private String name;
    private String coverUrl;
    private List<String> images;
    private String description;
    private String origin;
    private Integer status;

    public Product() {
    }

    public Product(Integer id, String name, String coverUrl, List<String> images, String description, String origin, Integer status) {
        this.id = id;
        this.name = name;
        this.coverUrl = coverUrl;
        this.images = images;
        this.description = description;
        this.origin = origin;
        this.status = status;
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
