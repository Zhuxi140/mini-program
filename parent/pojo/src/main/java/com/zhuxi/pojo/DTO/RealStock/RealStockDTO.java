package com.zhuxi.pojo.DTO.RealStock;

import io.swagger.v3.oas.annotations.media.Schema;

public class RealStockDTO {
    @Schema(description = "商品id",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId;
    @Schema(description = "规格id",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long specId;


    public RealStockDTO() {
    }

    public RealStockDTO(Long productId, Long specId) {
        this.productId = productId;
        this.specId = specId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getSpecId() {
        return specId;
    }

    public void setSpecId(Long specId) {
        this.specId = specId;
    }

}
