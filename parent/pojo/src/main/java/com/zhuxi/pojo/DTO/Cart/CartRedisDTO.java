package src.main.java.com.zhuxi.pojo.DTO.Cart;

import java.time.LocalDateTime;

public class CartRedisDTO {

    private Long carId;
    private Long productSnowflake;
    private Long specSnowflake;
    private Integer quantity;

    public CartRedisDTO(Long carId, Long productSnowflake, Long specSnowflake, Integer quantity) {
        this.carId = carId;
        this.productSnowflake = productSnowflake;
        this.specSnowflake = specSnowflake;
        this.quantity = quantity;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
