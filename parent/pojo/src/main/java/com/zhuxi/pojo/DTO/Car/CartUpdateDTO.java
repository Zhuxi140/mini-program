package src.main.java.com.zhuxi.pojo.DTO.Car;

import io.swagger.v3.oas.annotations.media.Schema;

public class CartUpdateDTO {
    @Schema(description = "购物车id",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long cartId ;
    @Schema(description = "规格id",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long specId;
    @Schema(description = "数量（若为第一次加入购物车，不需填写）",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer quantity = 1;

    public CartUpdateDTO() {
    }

    public CartUpdateDTO(Long cartId, Long specId, Integer quantity) {
        this.cartId = cartId;
        this.specId = specId;
        this.quantity = quantity;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
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
