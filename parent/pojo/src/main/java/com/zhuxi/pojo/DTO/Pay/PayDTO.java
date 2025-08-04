package src.main.java.com.zhuxi.pojo.DTO.Pay;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public class PayDTO {
    @JsonIgnore
    private Long OrderId;
    @Schema(description = "订单号",requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderSn;
    @Schema(description = "用户id",hidden = true)
    private Long userId;
    @Schema(description = "实付金额",requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal paidAmount;
    @Schema(description = "支付方式(1在线支付，2货到付款。未给值时后端默认为1)",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer paymentMethod = 1;
    @Schema(description = "订单数量",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;

    public PayDTO() {
    }

    public PayDTO(Long orderId, String orderSn, Long userId, BigDecimal paidAmount, Integer paymentMethod, Integer quantity) {
        OrderId = orderId;
        this.orderSn = orderSn;
        this.userId = userId;
        this.paidAmount = paidAmount;
        this.paymentMethod = paymentMethod;
        this.quantity = quantity;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


    public Long getOrderId() {
        return OrderId;
    }

    public void setOrderId(Long orderId) {
        OrderId = orderId;
    }
}
