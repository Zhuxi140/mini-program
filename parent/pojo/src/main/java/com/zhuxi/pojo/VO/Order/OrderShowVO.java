package com.zhuxi.pojo.VO.Order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderShowVO {
    @Schema(description = "订单id",hidden = true)
    @JsonIgnore
    private Long id;
    @Schema(description = "订单编号")
    private String orderSn;
    @Schema(description = "订单组id")
    private Long groupId;
    @Schema(description = "商品名称")
    private String productName;
    @Schema(description = "商品规格名称")
    private String specName;
    @Schema(description = "商品封面")
    private String coverUrl;
    @Schema(description = "订单金额")
    private BigDecimal totalAmount;
    @Schema(description = "订单状态(0待付款 1待发货 2待收货 3已完成 4已取消 5退款中 6已退款")
    private Integer status;
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public OrderShowVO() {
    }

    public OrderShowVO(Long id,String orderSn,Long groupId, String productName, String specName, String coverUrl, BigDecimal totalAmount, Integer status,LocalDateTime createdAt) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    @Override
    public String toString() {
        return "OrderShowVO{" +
                "id=" + id +
                ", orderSn='" + orderSn + '\'' +
                ", groupId=" + groupId +
                ", productName='" + productName + '\'' +
                ", specName='" + specName + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
