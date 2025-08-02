package src.main.java.com.zhuxi.pojo.DTO.product;

import java.math.BigDecimal;

public class SpecRedisDTO {

    private Long id;
    private Long productId;
    private Long snowflakeId;
    private String spec;
    private BigDecimal salePrice;
    private String coverUrl;
    private Integer stock;

    public SpecRedisDTO() {
    }

    public SpecRedisDTO(Long id, Long productId, Long snowflakeId,String spec, BigDecimal salePrice, String coverUrl, Integer stock) {
        this.id = id;
        this.productId = productId;
        this.snowflakeId = snowflakeId;
        this.spec = spec;
        this.salePrice = salePrice;
        this.coverUrl = coverUrl;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Long getSnowflakeId() {
        return snowflakeId;
    }

    public void setSnowflakeId(Long snowflakeId) {
        this.snowflakeId = snowflakeId;
    }

}
