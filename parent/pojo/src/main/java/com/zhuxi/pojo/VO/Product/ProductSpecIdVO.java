package com.zhuxi.pojo.VO.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecIdVO {
    private Long productId;
    private List<Long> specId = new ArrayList<>();

    public ProductSpecIdVO() {
    }

    public ProductSpecIdVO(Long productId, List<Long> specId) {
        this.productId = productId;
        this.specId = specId;
    }

    public List<Long> getSpecId() {
        return specId;
    }

    public void setSpecId(List<Long> specId) {
        this.specId = specId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
