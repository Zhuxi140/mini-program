package src.main.java.com.zhuxi.pojo.DTO.Order;


public class BloomOrderDTO {
    private Long id;
    private String orderSn;
    private Long userId;

    public BloomOrderDTO() {
    }

    public BloomOrderDTO(Long id, String orderSn, Long userId) {
        this.id = id;
        this.orderSn = orderSn;
        this.userId = userId;
    }



    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
}
