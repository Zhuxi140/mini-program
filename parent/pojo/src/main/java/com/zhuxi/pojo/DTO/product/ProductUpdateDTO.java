package src.main.java.com.zhuxi.pojo.DTO.product;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class ProductUpdateDTO {

    @Schema(description = "商品基础信息",requiredMode = Schema.RequiredMode.REQUIRED)
    private ProductBaseUpdateDTO base;
    @Schema(description = "商品规格信息",requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ProductSpecUpdateDTO> specs;

    public ProductUpdateDTO() {
    }

    public ProductUpdateDTO(ProductBaseUpdateDTO base, List<ProductSpecUpdateDTO> spec) {
        this.base = base;
        this.specs = spec;
    }

    public ProductBaseUpdateDTO getBase() {
        return base;
    }

    public void setBase(ProductBaseUpdateDTO base) {
        this.base = base;
    }

    public List<ProductSpecUpdateDTO> getSpec() {
        return specs;
    }

    public void setSpec(List<ProductSpecUpdateDTO> spec) {
        this.specs = spec;
    }
}
