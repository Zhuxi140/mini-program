package com.zhuxi.pojo.DTO.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

public class ProductBaseDTO {

    @Schema(description = "商品id",hidden = true)
    private Long id;

    @Schema(description = "商品名称",requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "商品编号",hidden = true)
    private Long productNumber;

    @Schema(description = "商品描述",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "商品产地(默认为河南省新乡市封丘县)",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String origin = "河南省新乡市封丘县";

    @Schema(description = "商品状态(0下架,1上架)",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonIgnore
    private Integer status = 0;


    public ProductBaseDTO(Long id, String name, Long productNumber, String description, String origin, Integer status ) {
        this.id = id;
        this.name = name;
        this.productNumber = productNumber;
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

    public Long getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(Long productNumber) {
        this.productNumber = productNumber;
    }
}
