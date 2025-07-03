package src.main.java.com.zhuxi.pojo.DTO.product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

public class ProductAddDTO {

    @Schema(description = "商品名称",requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Schema(description = "商品价格",requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal price;
    @Schema(description = "封面图",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String coverUrl;
    @Schema(description = "商品图片",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> images;
    @Schema(description = "商品描述",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;
    @Schema(description = "商品库存(默认为1)",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer stock = 1;
    @Schema(description = "商品产地(默认为河南省新乡市封丘县)",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String origin = "河南省新乡市封丘县";

    public ProductAddDTO(String name, BigDecimal price, String coverUrl, List<String> images, String description, Integer stock, String origin) {
        this.name = name;
        this.price = price;
        this.coverUrl = coverUrl;
        this.images = images;
        this.description = description;
        this.stock = stock;
        this.origin = origin;
    }

    public ProductAddDTO() {
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
}
