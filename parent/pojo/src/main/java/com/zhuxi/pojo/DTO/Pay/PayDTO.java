package src.main.java.com.zhuxi.pojo.DTO.Pay;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public class PayDTO {

    @Schema(description = "订单id",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderId;
    @Schema(description = "用户id",hidden = true)
    private Long userId;
    @Schema(description = "实付金额",requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal paidAmount;
    @Schema(description = "支付方式(1在线支付，2货到付款。未给值时后端默认为1)",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer paymentMethod = 1;

    public PayDTO() {
    }

    public PayDTO(Long orderId, Long userId, BigDecimal paidAmount, Integer paymentMethod) {
        this.orderId = orderId;
        this.userId = userId;
        this.paidAmount = paidAmount;
        this.paymentMethod = paymentMethod;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
