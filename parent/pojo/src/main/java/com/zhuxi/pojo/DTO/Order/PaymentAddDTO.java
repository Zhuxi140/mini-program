package src.main.java.com.zhuxi.pojo.DTO.Order;

import java.math.BigDecimal;

public class PaymentAddDTO {
    private String paymentSn;
    private Long userId;
    private Long orderId;
    private BigDecimal totalAmount;
    private Integer status = 0;

    public PaymentAddDTO() {
    }

    public PaymentAddDTO(String paymentSn, Long userId, Long orderId, BigDecimal totalAmount, Integer status) {
        this.paymentSn = paymentSn;
        this.userId = userId;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.status = 0;
    }

    public String getPaymentSn() {
        return paymentSn;
    }

    public void setPaymentSn(String paymentSn) {
        this.paymentSn = paymentSn;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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
}
