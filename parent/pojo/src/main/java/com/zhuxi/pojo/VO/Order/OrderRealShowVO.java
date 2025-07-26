package src.main.java.com.zhuxi.pojo.VO.Order;


import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class OrderRealShowVO {
    @Schema(description = "订单组id")
    private Long groupId;
    @Schema(description = "订单列表")
    private List<OrderShowVO> orderShowVO;

    public OrderRealShowVO() {
    }

    public OrderRealShowVO(Long groupId, List<OrderShowVO> orderShowVO) {
        this.groupId = groupId;
        this.orderShowVO = orderShowVO;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public void setOrderShowVO(List<OrderShowVO> orderShowVO) {
        this.orderShowVO = orderShowVO;
    }

    public List<OrderShowVO> getOrderShowVO() {
        return orderShowVO;
    }

    @Override
    public String toString() {
        return "OrderRealShowVO{" +
                "groupId=" + groupId +
                ", orderShowVO=" + orderShowVO.toString() +
                '}';
    }
}
