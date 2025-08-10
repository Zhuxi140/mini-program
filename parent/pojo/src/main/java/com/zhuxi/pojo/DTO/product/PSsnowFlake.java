package com.zhuxi.pojo.DTO.product;

import java.util.List;

public class PSsnowFlake {
    private Long ProductSnowflake;
    private List< Long> SpecSnowflake;

    public PSsnowFlake() {
    }

    public PSsnowFlake(Long productSnowflake, List<Long> specSnowflake) {
        ProductSnowflake = productSnowflake;
        SpecSnowflake = specSnowflake;
    }

    public Long getProductSnowflake() {
        return ProductSnowflake;
    }

    public void setProductSnowflake(Long productSnowflake) {
        ProductSnowflake = productSnowflake;
    }

    public List<Long> getSpecSnowflake() {
        return SpecSnowflake;
    }

    public void setSpecSnowflake(List<Long> specSnowflake) {
        SpecSnowflake = specSnowflake;
    }
}
