package src.main.java.com.zhuxi.pojo.VO.Order;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

public class OrderShowVO {
    @Schema(description = "订单id")
    private Long id;
    @Schema(description = "订单组id")
    private Long groupId;
    @Schema(description = "商品名称")
    private String productName;
    @Schema(description = "商品规格名称")
    private String specName;
    @Schema(description = "商品图片组")
    private List<String> coverUrl;
    @Schema(description = "订单金额")
    private BigDecimal totalAmount;
    @Schema(description = "订单状态(0待付款 1待发货 2待收货 3已完成 4已取消 5退款中 6已退款")
    private Integer status;

    public OrderShowVO() {
    }

    public OrderShowVO(Long id,Long groupId, String productName, String specName, List<String> coverUrl, BigDecimal totalAmount, Integer status) {
        this.id = id;
        this.groupId = groupId;
        this.productName = productName;
        this.specName = specName;
        this.coverUrl = coverUrl;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public void setCoverUrl(List<String> coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getCoverUrl() {
        return coverUrl;
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
}
