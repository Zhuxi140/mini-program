package src.main.java.com.zhuxi.pojo.VO.Admin;

import java.math.BigDecimal;

public class AdminProductVO {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String stockStatus;

    public AdminProductVO(Long id, String name, BigDecimal price, Integer stock, String stockStatus) {

        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.stockStatus = stockStatus;
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

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }
}
