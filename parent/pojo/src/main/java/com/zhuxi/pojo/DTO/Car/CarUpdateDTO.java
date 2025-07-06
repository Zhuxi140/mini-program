package src.main.java.com.zhuxi.pojo.DTO.Car;

import io.swagger.v3.oas.annotations.media.Schema;

public class CarUpdateDTO {

    @Schema(description = "商品id",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId ;
    @Schema(description = "规格id",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long specId;
    @Schema(description = "数量（若为第一次加入购物车，不需填写）",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity = 1;

    public CarUpdateDTO() {
    }

    public CarUpdateDTO(Long productId, Long specId, Integer quantity) {
        this.productId = productId;
        this.specId = specId;
        this.quantity = quantity;
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
}
