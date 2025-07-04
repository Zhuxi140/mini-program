package src.main.java.com.zhuxi.pojo.DTO.product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class ProductBaseDTO {

    @Schema(description = "商品id(在更新信息中需要给予id，添加中不需要给予)",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;
    @Schema(description = "商品名称",requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Schema(description = "商品封面图",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String coverUrl;
    @Schema(description = "商品图片",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> images;
    @Schema(description = "商品描述",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;
    @Schema(description = "商品产地(默认为河南省新乡市封丘县)",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String origin = "河南省新乡市封丘县";
    @Schema(description = "商品状态(0下架,1上架)",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer status = 1;

    public ProductBaseDTO(Long id,String name, String coverUrl, List<String> images, String description, String origin,Integer status ) {
        this.name = name;
        this.coverUrl = coverUrl;
        this.images = images;
        this.description = description;
        this.origin = origin;
        this.status = status;
    }

    public ProductBaseDTO() {
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
