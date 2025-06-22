package src.main.java.com.zhuxi.pojo.DTO.Car;

import io.swagger.v3.oas.annotations.media.Schema;

public class CarUpdateDTO {

    @Schema(description = "商品id",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId ;
    @Schema(description = "数量（若为第一次加入购物车，不需填写）",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity = 1;

    public CarUpdateDTO(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public CarUpdateDTO() {
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
