package src.main.java.com.zhuxi.pojo.VO.Product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductOverviewVO {

    @Schema(description = "商品id")
    private Long id;
    @Schema(description = "商品名称")
    private String name;
    @Schema(description = "商品价格")
    private BigDecimal price;
    @Schema(description = "商品封面图片")
    private String coverUrl;
    @JsonIgnore
    @Schema(description = "商品创建时间")
    private LocalDateTime createdAt;


    public ProductOverviewVO(Long id, String name, BigDecimal price, String coverUrl, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.coverUrl = coverUrl;
        this.createdAt = createdAt;
    }

    public ProductOverviewVO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createAt) {
        this.createdAt = createAt;
    }

}
