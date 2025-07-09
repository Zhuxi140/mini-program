package src.main.java.com.zhuxi.pojo.DTO.Order;

import java.math.BigDecimal;

public class OrderGroupDTO {
    private Long id;
    private String groupSn;
    private Long userId;
    private BigDecimal totalAmount;
    private Integer paymentStatus;

    public OrderGroupDTO() {
    }

    public OrderGroupDTO(Long id, String groupSn, Long userId, BigDecimal totalAmount, Integer paymentStatus) {
        this.id = id;
        this.groupSn = groupSn;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupSn() {
        return groupSn;
    }

    public void setGroupSn(String groupSn) {
        this.groupSn = groupSn;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Integer paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
