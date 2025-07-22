package src.main.java.com.zhuxi.pojo.DTO.Order;


public class BloomOrderDTO {
    private Long id;
    private Long userId;

    public BloomOrderDTO() {
    }

    public BloomOrderDTO(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


}
