package com.zhuxi.pojo.DTO.product;

public class snowFlakeMap {

    private Long productSnowFlake;
    private Long specSnowFlake;

    public snowFlakeMap() {
    }

    public snowFlakeMap(Long productSnowFlake, Long specSnowFlake) {
        this.productSnowFlake = productSnowFlake;
        this.specSnowFlake = specSnowFlake;
    }

    public Long getProductSnowFlake() {
        return productSnowFlake;
    }

    public void setProductSnowFlake(Long productSnowFlake) {
        this.productSnowFlake = productSnowFlake;
    }

    public Long getSpecSnowFlake() {
        return specSnowFlake;
    }

    public void setSpecSnowFlake(Long specSnowFlake) {
        this.specSnowFlake = specSnowFlake;
    }
}
