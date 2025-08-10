package src.main.java.com.zhuxi.pojo.DTO.Cart;

public class MQdelete {
    private Long cartId;
    private Long userId;

    public MQdelete() {
    }

    public MQdelete(Long cartId, Long userId) {
        this.cartId = cartId;
        this.userId = userId;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
