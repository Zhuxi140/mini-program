package com.zhuxi.pojo.DTO.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

public class SpecAddDTO {

    @JsonIgnore
    private Long id;
    @Schema(description = "雪花Id",hidden = true)
    private Long snowflakeId;
    @Schema(description = "商品id",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId;
    @Schema(description = "规格",requiredMode = Schema.RequiredMode.REQUIRED)
    private String spec;

    public SpecAddDTO() {
    }

    public SpecAddDTO(Long id, Long snowflakeId, Long productId, String spec) {
        this.id = id;
        this.snowflakeId = snowflakeId;
        this.productId = productId;
        this.spec = spec;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSnowflakeId() {
        return snowflakeId;
    }

    public void setSnowflakeId(Long snowFlakeId) {
        this.snowflakeId = snowFlakeId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

}
