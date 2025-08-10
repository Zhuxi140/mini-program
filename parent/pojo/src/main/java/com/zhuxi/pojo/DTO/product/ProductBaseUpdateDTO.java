package com.zhuxi.pojo.DTO.product;

import io.swagger.v3.oas.annotations.media.Schema;

public class ProductBaseUpdateDTO {
    @Schema(description = "商品id",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;
    @Schema(description = "供应商id")
    private Integer supplierId;
    @Schema(description = "商品名称")
    private String name;
    @Schema(description = "商品描述")
    private String description;
    @Schema(description = "商品产地")
    private String origin;


    public ProductBaseUpdateDTO() {
    }

    public ProductBaseUpdateDTO(Long id, Integer supplierId, String name, String description, String origin) {
        this.id = id;
        this.supplierId = supplierId;
        this.name = name;
        this.description = description;
        this.origin = origin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
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

}
