package src.main.java.com.zhuxi.pojo.DTO.Order;

public class InventoryLockAddDTO {

    private Long productId;
    private Long orderId;
    private Long specId;
    private Integer quantity;

    public InventoryLockAddDTO() {
    }

    public InventoryLockAddDTO(Long productId, Long orderId, Long specId, Integer quantity) {
        this.productId = productId;
        this.orderId = orderId;
        this.specId = specId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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
