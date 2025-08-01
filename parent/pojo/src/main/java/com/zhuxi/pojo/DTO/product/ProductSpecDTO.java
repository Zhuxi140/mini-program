package src.main.java.com.zhuxi.pojo.DTO.product;

import io.swagger.v3.oas.annotations.media.Schema;


public class ProductSpecDTO {

    @Schema(description = "商品规格id",hidden = true)
    private Long id;
    @Schema(description = "商品规格(至少有一个规格)",requiredMode = Schema.RequiredMode.REQUIRED)
    private String spec;
    @Schema(description = "规格号",hidden = true)
    private Long specNumber;


    public ProductSpecDTO(Long id, String spec, Long specNumber) {
        this.id = id;
        this.spec = spec;
        this.specNumber = specNumber;
    }

    public ProductSpecDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public Long getSpecNumber() {
        return specNumber;
    }

    public void setSpecNumber(Long specNumber) {
        this.specNumber = specNumber;
    }
}
