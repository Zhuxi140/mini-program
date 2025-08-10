package com.zhuxi.pojo.DTO.product;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class ProductAddDTO {

    @Schema(description = "商品基础信息",requiredMode = Schema.RequiredMode.REQUIRED)
    private ProductBaseDTO base;
    @Schema(description = "商品规格信息",requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ProductSpecDTO> specs;

    public ProductAddDTO() {
    }

    public ProductAddDTO(ProductBaseDTO base, List<ProductSpecDTO> spec) {
        this.base = base;
        this.specs = spec;
    }

    public ProductBaseDTO getBase() {
        return base;
    }

    public void setBase(ProductBaseDTO base) {
        this.base = base;
    }

    public List<ProductSpecDTO> getSpec() {
        return specs;
    }

    public void setSpec(List<ProductSpecDTO> spec) {
        this.specs = spec;
    }
}
