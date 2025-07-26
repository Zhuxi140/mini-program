package src.main.java.com.zhuxi.pojo.DTO.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderRedisDTO {
    private Long id;
    private String orderSn;
    private Long groupId;
    private String productName;
    private String specName;
    private String coverUrl;
    private BigDecimal totalAmount;
    private int status;
    private LocalDateTime createdAt;

    public OrderRedisDTO() {
    }

    public OrderRedisDTO(Long id,String orderSn, Long groupId, String productName, String specName, String coverUrl, BigDecimal totalAmount, int status,LocalDateTime createdAt) {
        this.id = id;
        this.orderSn = orderSn;
        this.groupId = groupId;
        this.productName = productName;
        this.specName = specName;
        this.coverUrl = coverUrl;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
