package src.main.java.com.zhuxi.pojo.entity;

public class InventoryLock {

    private Long id;
    private Long orderId;
    private Long productId;
    private Long specId;
    private Integer quantity;
    private Integer status;

    public InventoryLock() {
    }

    public InventoryLock(Long id, Long orderId, Long productId, Long specId, Integer quantity, Integer status) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.specId = specId;
        this.quantity = quantity;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
