package src.main.java.com.zhuxi.pojo.DTO.Car;

import io.swagger.v3.oas.annotations.media.Schema;

public class CartAddDTO {

    @Schema(description = "商品id",hidden = true)
    private Long productSnowflake ;
    @Schema(description = "规格号",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long specSnowflake;
    @Schema(description = "数量（后台默认添加数量为1）",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity = 1;
    @Schema(description = "商品id",hidden = true)
    private Long productId;
    @Schema(description = "规格id",hidden = true)
    private Long specId;

    public CartAddDTO() {
    }

    public CartAddDTO(Long productSnowflake, Long specSnowflake, Integer quantity, Long productId, Long specId) {
        this.productSnowflake = productSnowflake;
        this.specSnowflake = specSnowflake;
        this.quantity = quantity;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getProductSnowflake() {
        return productSnowflake;
    }

    public void setProductSnowflake(Long productSnowflake) {
        this.productSnowflake = productSnowflake;
    }

    public Long getSpecSnowflake() {
        return specSnowflake;
    }

    public void setSpecSnowflake(Long specSnowflake) {
        this.specSnowflake = specSnowflake;
    }
}
