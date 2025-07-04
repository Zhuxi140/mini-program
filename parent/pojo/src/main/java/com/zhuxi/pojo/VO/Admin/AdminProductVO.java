package src.main.java.com.zhuxi.pojo.VO.Admin;


public class AdminProductVO {
    private Long id;
    private String name;
    private Integer stock;
    private String specNames;
    private String stockStatus;

    public AdminProductVO(Long id, String name, Integer stock,String specNames, String stockStatus) {

        this.id = id;
        this.name = name;
        this.stock = stock;
        this.specNames = specNames;
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

    public String getSpecNames() {
        return specNames;
    }

    public void setSpecNames(String specNames) {
        this.specNames = specNames;
    }
}
